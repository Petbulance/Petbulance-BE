package com.example.Petbulance_BE.domain.post.service;

import com.example.Petbulance_BE.domain.board.entity.Board;
import com.example.Petbulance_BE.domain.board.repository.BoardRepository;
import com.example.Petbulance_BE.domain.post.dto.request.CreatePostReqDto;
import com.example.Petbulance_BE.domain.post.dto.request.UpdatePostReqDto;
import com.example.Petbulance_BE.domain.post.dto.response.*;
import com.example.Petbulance_BE.domain.post.entity.Post;
import com.example.Petbulance_BE.domain.post.entity.PostImage;
import com.example.Petbulance_BE.domain.post.repository.PostImageRepository;
import com.example.Petbulance_BE.domain.post.repository.PostLikeRepository;
import com.example.Petbulance_BE.domain.post.repository.PostRepository;
import com.example.Petbulance_BE.domain.post.repository.PostViewCountRepository;
import com.example.Petbulance_BE.domain.post.type.Category;
import com.example.Petbulance_BE.domain.user.entity.Users;
import com.example.Petbulance_BE.global.common.error.exception.CustomException;
import com.example.Petbulance_BE.global.common.error.exception.ErrorCode;
import com.example.Petbulance_BE.global.util.TimeUtil;
import com.example.Petbulance_BE.global.util.UserUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Slice;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class PostService {
    private final PostRepository postRepository;
    private final BoardRepository boardRepository;
    private final PostImageRepository postImageRepository;
    private final PostViewCountRepository postViewCountRepository;
    private final RedisTemplate<String, Object> redisTemplate;
    private final PostLikeRepository postLikeRepository;
    private static final String CACHE_KEY_FORMAT = "post::inquiry::%d";

    @Transactional
    public CreatePostResDto createPost(CreatePostReqDto dto) {

        if (dto.getTitle().isBlank() || dto.getContent().isBlank()) {
            throw new CustomException(ErrorCode.EMPTY_TITLE_OR_CONTENT);
        }
        if (dto.getImageUrls() != null && dto.getImageUrls().size() > 10) {
            throw new CustomException(ErrorCode.EXCEEDED_MAX_IMAGE_COUNT);
        }

        Board board = boardRepository.findById(dto.getBoardId())
                .orElseThrow(() -> new CustomException(ErrorCode.INVALID_BOARD_OR_CATEGORY));

        Category category;
        try {
            category = Category.valueOf(dto.getCategory());
        } catch (IllegalArgumentException e) {
            throw new CustomException(ErrorCode.INVALID_BOARD_OR_CATEGORY);
        }

        Post post = Post.builder()
                .board(board)
                .user(UserUtil.getCurrentUser())
                .category(category)
                .title(dto.getTitle())
                .content(dto.getContent())
                .hidden(false)
                .deleted(false)
                .imageNum(Optional.ofNullable(dto.getImageUrls()).orElse(List.of()).size())
                .build();

        Post savedPost = postRepository.save(post);

        savePostImages(savedPost, dto.getImageUrls());

        return CreatePostResDto.of(savedPost, savedPost.getBoard().getId(), dto.getImageUrls());
    }

    private void savePostImages(Post post, List<String> imageUrls) {
        if (imageUrls == null || imageUrls.isEmpty()) return;

        for (int i = 0; i < imageUrls.size(); i++) {
            boolean isThumbnail = (i == 0); // 첫 번째 이미지를 썸네일로 설정
            PostImage postImage = PostImage.create(post, imageUrls.get(i), i+1, isThumbnail);
            postImageRepository.save(postImage);
        }
    }

    @Transactional
    public InquiryPostResDto inquiryPost(Long postId) {
        Post post = validateVisiblePost(postId);

        // 현재 로그인 유저 (게시글 작성자인지 확인하기 위함)
        Users currentUser = UserUtil.getCurrentUser();
        boolean currentUserIsPostAuthor = currentUserIsPostAuthor(post.getUser(), currentUser); // 현재 유저가 게시글 작성자인지

        // Redis 기반 조회수 증가
        assert currentUser != null;
        long viewCount = postViewCountRepository.increaseIfNotViewed(postId, currentUser.getId()); // 해당 게시글을 조회한 적 없는 사용자에 대해서만 카윤트 집계

        // 정적 데이터 캐싱
        String key = String.format(CACHE_KEY_FORMAT, postId); // 키 생성
        InquiryPostResDto cachedDto = (InquiryPostResDto) redisTemplate.opsForValue().get(key); // 캐싱된 데이터 조회

        // 캐시 미스 시 DB 조회 후 캐싱
        if (cachedDto == null) {
            cachedDto = postRepository.findInquiryPost(post, currentUserIsPostAuthor, currentUser, viewCount);
            if (cachedDto != null) { // DB에서 조회된 상세 정보를 캐시에 저장 TTL
                redisTemplate.opsForValue().set(key, cachedDto, Duration.ofHours(6)); // TTL 6시간
            }
        }

        if (cachedDto == null) { // 정적 데이터 조회 실패시
            throw new CustomException(ErrorCode.INTERNAL_SERVER_ERROR);
        }

        // 실시간 데이터 업데이트 (좋아요, 댓글, 조회수, 좋아요 여부)
        InquiryPostResDto.PostInfo updated = cachedDto.getPost().toBuilder()
                .createdAt(TimeUtil.formatCreatedAt(LocalDateTime.parse(cachedDto.getPost().getCreatedAt()))) // 시간 형식 변경
                .likeCount(postRepository.fetchLikeCount(postId))
                .commentCount(postRepository.fetchCommentCount(postId))
                .viewCount((int) viewCount)
                .likedByUser(postRepository.fetchLikedByUser(currentUser, postId))
                .isCurrentUserPost(currentUserIsPostAuthor)
                .build();

        return InquiryPostResDto.builder()
                .board(cachedDto.getBoard())
                .post(updated)
                .build();
    }

    private Post getPost(Long postId) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new CustomException(ErrorCode.POST_NOT_FOUND)); // 현재 조회하는 게시글
        return post;
    }


    private boolean currentUserIsPostAuthor(Users postAuthor, Users currentUser) {
        return currentUser == postAuthor; // 현재 로그인 유저와 게시글 작성자가 동일 유저인지 판별
    }

    @Transactional(readOnly = true)
    public PagingPostListResDto postList(Long boardId, String category, String sort, Long lastPostId, Integer pageSize) {

        // 카테고리/정렬/게시판 검증
        Category c = null;
        if (category != null && !category.isBlank() && Category.isValidCategory(category)) {
            c = Category.valueOf(category);
        }
        validateSortCondition(sort);
        validationBoardId(boardId);

        // 게시글 목록 조회
        Slice<PostListResDto> postSlice = postRepository.findPostList(boardId, c, sort, lastPostId, pageSize);
        List<PostListResDto> posts = postSlice.getContent();

        if (posts.isEmpty()) {
            return new PagingPostListResDto(postSlice); // 내용이 없으면 그대로 반환
        }

        Users currentUser = UserUtil.getCurrentUser();
        // 게시글 ID 리스트 추출
        List<Long> postIds = posts.stream()
                .map(PostListResDto::getPostId)
                .toList();

        // Redis에서 조회수 일괄 조회
        Map<Long, Long> viewCountMap = postViewCountRepository.readAll(postIds);

        // DB에서 현재 유저의 좋아요 여부 일괄 조회
        Set<Long> likedPostIds = postLikeRepository.findLikedPostIdsByUserAndPostIds(currentUser, postIds);

        // DTO에 매핑
        posts.forEach(dto -> {
            dto.setViewCount(viewCountMap.getOrDefault(dto.getPostId(), 0L));
            dto.setLikedByUser(likedPostIds.contains(dto.getPostId()));
            dto.setCreatedAt(TimeUtil.formatCreatedAt(LocalDateTime.parse(dto.getCreatedAt())));
        });

        return new PagingPostListResDto(postSlice);
    }


    private void validationBoardId(Long boardId) {
        if (boardId != null && !boardRepository.existsById(boardId)) {
            throw new CustomException(ErrorCode.BOARD_NOT_FOUND);
        }
    }

    private void validateSortCondition(String sort) {
        if (!("popular".equalsIgnoreCase(sort)
                || "latest".equalsIgnoreCase(sort)
                || "comment".equalsIgnoreCase(sort))) {
            throw new CustomException(ErrorCode.INVALID_SORT_CONDITION);
        }
    }


    @Transactional(readOnly = true)
    public PagingPostSearchListResDto postSearchList(Long boardId, List<String> category, String sort, Long lastPostId, Integer pageSize, String searchKeyword, String searchScope) {
        Category.convertToCategoryList(category);
        validateSortCondition(sort);
        validationBoardId(boardId);
        validateSearchScope(searchScope);

        PagingPostSearchListResDto pagingResult =
                postRepository.findPostSearchList(boardId, category, sort, lastPostId, pageSize, searchKeyword, searchScope);

        List<PostSearchListResDto> posts = pagingResult.getContent();
        if (posts.isEmpty()) {
            return pagingResult;
        }

        Users currentUser = UserUtil.getCurrentUser();

        // postId 추출
        List<Long> postIds = posts.stream()
                .map(PostSearchListResDto::getPostId)
                .toList();

        // Redis에서 조회수 일괄 조회
        Map<Long, Long> viewCountMap = postViewCountRepository.readAll(postIds);

        // 좋아요 여부 일괄 조회 (Batch Query)
        Set<Long> likedPostIds = postLikeRepository.findLikedPostIdsByUserAndPostIds(currentUser, postIds);

        // DTO 매핑
        posts.forEach(dto -> {
            dto.setViewCount(viewCountMap.getOrDefault(dto.getPostId(), 0L));
            dto.setLikedByUser(likedPostIds.contains(dto.getPostId()));
            dto.setCreated(TimeUtil.formatCreatedAt(LocalDateTime.parse(dto.getCreated())));
        });

        return pagingResult;
    }

    private void validateSearchScope(String searchScope) {
        if (!("title_content".equalsIgnoreCase(searchScope)
                || "title".equalsIgnoreCase(searchScope)
                || "writer".equalsIgnoreCase(searchScope))) {
            throw new CustomException(ErrorCode.INVALID_SEARCH_SCOPE);
        }
    }

    public UpdatePostResDto updatePost(Long postId, UpdatePostReqDto dto) {
        Post post = validateVisiblePost(postId);

        // 현재 로그인 유저 (게시글 작성자인지 확인하기 위함)
        Users currentUser = UserUtil.getCurrentUser();
        boolean currentUserIsPostAuthor = currentUserIsPostAuthor(post.getUser(), currentUser); // 현재 유저가 게시글 작성자인지
        if(!currentUserIsPostAuthor) {
            throw new CustomException(ErrorCode.FORBIDDEN_POST_ACCESS);
        }

        if (dto.getTitle().isBlank() || dto.getContent().isBlank()) {
            throw new CustomException(ErrorCode.EMPTY_TITLE_OR_CONTENT);
        }
        if (dto.getImageUrls() != null && dto.getImageUrls().size() > 10) {
            throw new CustomException(ErrorCode.EXCEEDED_MAX_IMAGE_COUNT);
        }

        Category c = null;
        if(Category.isValidCategory(dto.getCategory())) {
            c = Category.valueOf(dto.getCategory());
        }

        return null;
    }

    private Post validateVisiblePost(Long postId) {
        Post post = getPost(postId);

        if (post.isHidden()) {
            throw new CustomException(ErrorCode.POST_HIDDEN); // 숨긴 게시글
        }
        if (post.isDeleted()) {
            throw new CustomException(ErrorCode.POST_DELETED); // 삭제된 게시글
        }
        return post;
    }

}
