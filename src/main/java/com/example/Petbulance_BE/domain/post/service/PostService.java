package com.example.Petbulance_BE.domain.post.service;

import com.example.Petbulance_BE.domain.board.entity.Board;
import com.example.Petbulance_BE.domain.board.repository.BoardRepository;
import com.example.Petbulance_BE.domain.post.dto.request.CreatePostReqDto;
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
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Slice;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Transactional
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

    public InquiryPostResDto inquiryPost(Long postId) {
        // 게시글 검증
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new CustomException(ErrorCode.POST_NOT_FOUND)); // 현재 조회하는 게시글

        if (post.isHidden()) throw new CustomException(ErrorCode.POST_HIDDEN); // 숨긴 게시글
        if (post.isDeleted()) throw new CustomException(ErrorCode.POST_DELETED); // 삭제된 게시글

        // 현재 로그인 유저
        Users currentUser = UserUtil.getCurrentUser();
        boolean currentUserIsPostAuthor = currentUserIsPostAuthor(post.getUser(), currentUser); // 현재 유저가 게시글 작성자인지

        // Redis 기반 조회수 증가
        assert currentUser != null;
        long viewCount = postViewCountRepository.increaseIfNotViewed(postId, currentUser.getId());

        // 정적 데이터 캐싱
        String key = String.format(CACHE_KEY_FORMAT, postId); // 키 생성
        InquiryPostResDto cachedDto = (InquiryPostResDto) redisTemplate.opsForValue().get(key); // 캐싱된 데이터 조회

        // 캐시 미스 시 DB 조회 후 캐싱
        if (cachedDto == null) {
            cachedDto = postRepository.findInquiryPost(post, currentUserIsPostAuthor, currentUser, viewCount);
            if (cachedDto != null) {
                redisTemplate.opsForValue().set(key, cachedDto, Duration.ofMinutes(10)); // TTL 10분
            }
        }

        if (cachedDto == null) {
            throw new CustomException(ErrorCode.INTERNAL_SERVER_ERROR);
        }

        // 실시간 데이터 업데이트 (좋아요, 댓글, 조회수, 좋아요 여부)
        InquiryPostResDto.PostInfo updated = cachedDto.getPost().toBuilder()
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


    private boolean currentUserIsPostAuthor(Users postAuthor, Users currentUser) {
        return UserUtil.getCurrentUser() == postAuthor;
    }

    public PagingPostListResDto postList(Long boardId, String category, String sort, Long lastPostId, Integer pageSize) {
        Category c = null;
        if(!category.isBlank() && Category.isValidCategory(category)) {
            c = Category.valueOf(category);
        }
        validateSortCondition(sort);
        validationBoardId(boardId);

        Slice<PostListResDto> postSlice = postRepository.findPostList(boardId, c, sort, lastPostId, pageSize);

        postSlice.getContent().forEach(dto -> {
            Long viewCount = postViewCountRepository.read(dto.getPostId());
            dto.setViewCount(viewCount != null ? viewCount : 0L);

            boolean likedByUser = postLikeRepository.existsByPostIdAndUser(dto.getPostId(), UserUtil.getCurrentUser());
            dto.setLikedByUser(likedByUser);

            dto.setCreated(TimeUtil.formatCreatedAt(LocalDateTime.parse(dto.getCreated())));
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


    public PagingPostSearchListResDto postSearchList(Long boardId, List<String> category, String sort, Long lastPostId, Integer pageSize, String searchKeyword, String searchScope) {
        Category.convertToCategoryList(category);
        validateSortCondition(sort);
        validationBoardId(boardId);
        validateSearchScope(searchScope);

        PagingPostSearchListResDto pagingResult =
                postRepository.findPostSearchList(boardId, category, sort, lastPostId, pageSize, searchKeyword, searchScope);

        pagingResult.getContent().forEach(dto -> {
            Long viewCount = postViewCountRepository.read(dto.getPostId());
            dto.setViewCount(viewCount != null ? viewCount : 0L);

            boolean likedByUser = postLikeRepository.existsByPostIdAndUser(dto.getPostId(), UserUtil.getCurrentUser());
            dto.setLikedByUser(likedByUser);

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

}
