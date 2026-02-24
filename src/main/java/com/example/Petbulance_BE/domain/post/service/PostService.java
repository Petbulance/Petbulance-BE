package com.example.Petbulance_BE.domain.post.service;

import com.example.Petbulance_BE.domain.adminlog.repository.AdminActionLogRepository;
import com.example.Petbulance_BE.domain.board.repository.BoardRepository;
import com.example.Petbulance_BE.domain.dashboard.service.DashboardMetricRedisService;
import com.example.Petbulance_BE.domain.notice.repository.NoticeRepository;
import com.example.Petbulance_BE.domain.post.dto.request.CreatePostReqDto;
import com.example.Petbulance_BE.domain.post.dto.request.UpdatePostReqDto;
import com.example.Petbulance_BE.domain.post.dto.response.*;
import com.example.Petbulance_BE.domain.post.entity.Post;
import com.example.Petbulance_BE.domain.post.entity.PostImage;
import com.example.Petbulance_BE.domain.post.repository.PostImageRepository;
import com.example.Petbulance_BE.domain.post.repository.PostLikeRepository;
import com.example.Petbulance_BE.domain.post.repository.PostRepository;
import com.example.Petbulance_BE.domain.post.repository.PostViewCountRepository;
import com.example.Petbulance_BE.domain.post.type.Topic;
import com.example.Petbulance_BE.domain.recent.service.RecentService;
import com.example.Petbulance_BE.domain.report.aop.communityBan.CheckCommunityAvailable;
import com.example.Petbulance_BE.domain.user.entity.Users;
import com.example.Petbulance_BE.global.common.error.exception.CustomException;
import com.example.Petbulance_BE.global.common.error.exception.ErrorCode;
import com.example.Petbulance_BE.global.common.s3.S3Service;
import com.example.Petbulance_BE.global.common.type.AnimalType;
import com.example.Petbulance_BE.global.util.TimeUtil;
import com.example.Petbulance_BE.global.util.UserUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class PostService {
    private final PostRepository postRepository;
    private final BoardRepository boardRepository;
    private final PostImageRepository postImageRepository;
    private final PostViewCountRepository postViewCountRepository;
    private final RedisTemplate<String, Object> redisTemplate;
    private final PostLikeRepository postLikeRepository;
    private final RecentService recentService;
    private final DashboardMetricRedisService dashboardMetricRedisService;
    private final AdminActionLogRepository adminActionLogRepository;
    private final NoticeRepository noticeRepository;
    private final S3Service s3Service;

    private static final String CACHE_KEY_FORMAT = "post::inquiry::%d";

    @Transactional
    @CheckCommunityAvailable
    public CreatePostResDto createPost(CreatePostReqDto dto) {
        if (dto.getImageUrls() != null && dto.getImageUrls().size() > 10) {
            throw new CustomException(ErrorCode.EXCEEDED_MAX_IMAGE_COUNT);
        }

        Post savedPost = postRepository.save(
                Post.builder()
                        .user(UserUtil.getCurrentUser())
                        .topic(dto.getTopic())
                        .animalType(dto.getType())
                        .title(dto.getTitle())
                        .content(dto.getContent())
                        .hidden(false)
                        .deleted(false)
                        .imageNum(Optional.ofNullable(dto.getImageUrls()).orElse(List.of()).size())
                        .build()); // 게시글 저장

        try {
            dashboardMetricRedisService.incrementTodayPostCreated();
        } catch (Exception e) {
            log.warn("Failed to increment post_created_count", e);
        }

        savePostImages(savedPost, dto.getImageUrls()); // 게시글 첨부 이미지 저장

        return CreatePostResDto.of(savedPost, dto.getImageUrls());
    }

    private void savePostImages(Post post, List<String> imageUrls) {
        if (imageUrls == null || imageUrls.isEmpty()) return;

        for (int i = 0; i < imageUrls.size(); i++) {
            String imageUrl = imageUrls.get(i);
            String key = s3Service.extractKeyFromUrl(imageUrl);

            if (!s3Service.doesObjectExist(key)) {
                throw new CustomException(ErrorCode.FAIL_IMAGE_UPLOAD);
            }

            boolean isThumbnail = (i == 0); // 첫 번째 이미지를 썸네일로 설정
            postImageRepository.save(PostImage.create(post, imageUrls.get(i), i + 1, isThumbnail));
        }
    }

    @Transactional
    @CheckCommunityAvailable
    public UpdatePostResDto updatePost(Long postId, UpdatePostReqDto dto) {
        Post post = validateVisiblePost(postId); // postId를 이용하여 게시글을 찾고 숨겨지거나 삭제된 게시글의 경우 예외 발생

        Users currentUser = UserUtil.getCurrentUser();
        if (!currentUserIsPostAuthor(post.getUser(), currentUser)) { // 현재 유저가 게시글 작성자인지 -> 수정권한이 있는지 확인
            throw new CustomException(ErrorCode.FORBIDDEN_POST_ACCESS);
        }

        if (dto.getImagesToKeepOrAdd() != null && dto.getImagesToKeepOrAdd().size() > 10) {
            throw new CustomException(ErrorCode.EXCEEDED_MAX_IMAGE_COUNT);
        }

        // 이미지 업데이트
        updatePostImages(post, dto);

        // 게시글 본문 수정
        post.update(dto.getTitle(), dto.getContent(), dto.getTopic(), dto.getType(), dto.getImagesToKeepOrAdd().size());

        return UpdatePostResDto.from(post, postImageRepository.findByPost(post));
    }

    @Transactional
    @CheckCommunityAvailable
    public void updatePostImages(Post post, UpdatePostReqDto dto) {
        List<PostImage> existingImages = postImageRepository.findByPost(post); // 해당 게시글의 첨부된 이미지 조회
        /*
        * 키(key): imageUrl
          값(value): PostImage 엔티티 자체
        */
        Map<String, PostImage> existingMap = existingImages.stream()
                .collect(Collectors.toMap(PostImage::getImageUrl, Function.identity()));

        // 삭제 처리
        if (dto.getImageUrlsToDelete() != null) {
            dto.getImageUrlsToDelete().forEach(url -> {
                PostImage target = existingMap.remove(url);
                if (target != null) {
                    postImageRepository.delete(target);
                    ;
                    try {
                        String key = s3Service.extractKeyFromUrl(url);
                        s3Service.deleteObject(key);
                    } catch (Exception e) {
                        log.warn("S3 파일 삭제 실패: {}", url, e);
                    }
                }
            });
        }

        // 추가/유지/순서/썸네일 갱신
        for (UpdatePostReqDto.ImageUpdateDto updateDto : dto.getImagesToKeepOrAdd()) {
            String imageUrl = updateDto.getImageUrl();

            // 1) S3 URL 형식/버킷 검증 + 2) key 추출 + 3) 존재 확인
            String key = s3Service.extractKeyFromUrl(imageUrl);
            if (!s3Service.doesObjectExist(key)) {
                throw new CustomException(ErrorCode.FAIL_IMAGE_UPLOAD); // 또는 IMAGE_NOT_FOUND 같은 코드
            }

            PostImage existing = existingMap.get(imageUrl);
            if (existing != null) {
                existing.updateOrderAndThumbnail(updateDto.getImageOrder(), updateDto.isThumbnail());
            } else {
                PostImage newImage = PostImage.create(
                        post,
                        updateDto.getImageUrl(),
                        updateDto.getImageOrder(),
                        updateDto.isThumbnail()
                );
                postImageRepository.save(newImage);
            }
        }
    }

    @Transactional
    @CheckCommunityAvailable
    public DeletePostResDto deletePost(Long postId) {
        Post post = validateVisiblePost(postId);

        Users currentUser = UserUtil.getCurrentUser();
        if (!currentUserIsPostAuthor(post.getUser(), currentUser)) { // 현재 유저가 게시글 작성자인지 -> 삭제권한이 있는지 확인
            throw new CustomException(ErrorCode.FORBIDDEN_POST_ACCESS);
        }

        postRepository.delete(post);

        return new DeletePostResDto(postId, true, post.isHidden(), LocalDateTime.now());
    }

    @Transactional
    @CheckCommunityAvailable
    public BulkDeletePostResDto deletePosts(List<Long> postIds) {

        // 1. 게시글 리스트 조회
        List<Post> posts = postRepository.findAllById(postIds); // 삭제하고자 하는 게시글 전체 조회

        if (posts.size() != postIds.size()) {
            throw new CustomException(ErrorCode.POST_NOT_FOUND);
        }

        // 2. 현재 로그인 유저 가져오기
        Users currentUser = UserUtil.getCurrentUser();

        // 3. 모든 게시글의 작성자가 현재 유저인지 확인
        boolean hasUnauthorizedPost = posts.stream()
                .anyMatch(post -> !currentUserIsPostAuthor(post.getUser(), currentUser));

        if (hasUnauthorizedPost) {
            throw new CustomException(ErrorCode.FORBIDDEN_POST_ACCESS);
        }

        // 4. 게시글 일괄 삭제
        postRepository.deleteAll(posts);

        // 5. 응답 생성
        List<BulkDeletePostItemDto> deletedItems = posts.stream()
                .map(post -> new BulkDeletePostItemDto(
                        post.getId(),
                        true,
                        LocalDateTime.now()
                ))
                .toList();

        return new BulkDeletePostResDto(deletedItems);
    }


    @Transactional(readOnly = true)
    @CheckCommunityAvailable
    public DetailPostResDto detailPost(Long postId) {
        Post post = validateVisiblePost(postId); // 숨김 게시글이나 삭제된 게시글 볼 수 없음

        // 현재 로그인 유저 (게시글 작성자인지 확인하기 위함)
        Users currentUser = UserUtil.getCurrentUser();
        boolean currentUserIsPostAuthor = currentUserIsPostAuthor(post.getUser(), currentUser); // 현재 유저가 게시글 작성자인지

        // Redis 기반 조회수 증가
        long viewCount = postViewCountRepository.increaseIfNotViewed(postId, currentUser == null ? null : currentUser.getId()); // 해당 게시글을 조회한 적 없는 사용자에 대해서만 카윤트 집계

        // 이미지 조회
        List<PostImage> postImages = postImageRepository
                .findByPostIdOrderByImageOrderAsc(postId);

        // DTO 매핑
        List<DetailPostResDto.ImageInfo> imageInfos = postImages.stream()
                .map(image -> DetailPostResDto.ImageInfo.builder()
                        .imageId(image.getId())
                        .imageUrl(image.getImageUrl())
                        .imageOrder(image.getImageOrder())
                        .thumbnail(image.isThumbnail())
                        .build()
                )
                .toList();

        DetailPostResDto.PostInfo updated = DetailPostResDto.PostInfo.builder()
                .postId(post.getId())
                .type(post.getAnimalType().getDescription())
                .topic(post.getTopic().getDescription())
                .title(post.getTitle())
                .content(post.getContent())
                .images(imageInfos)
                .writerNickname(post.getUser().getNickname())
                .writerProfileUrl(post.getUser().getProfileImage())
                .createdAt(TimeUtil.formatCreatedAt(post.getCreatedAt())) // 시간 형식 변경
                .likeCount(postRepository.fetchLikeCount(postId))
                .commentCount(postRepository.fetchCommentCount(postId))
                .viewCount((int) viewCount)
                .likedByUser(postRepository.fetchLikedByUser(currentUser, postId))
                .isCurrentUserPost(currentUserIsPostAuthor)
                .build();

        return DetailPostResDto.builder()
                .post(updated)
                .build();
    }

    @Transactional(readOnly = true)
    @CheckCommunityAvailable
    public PagingPostListResDto postList(AnimalType type, Topic topic, String sort, Long lastPostId, Integer pageSize) {
        validateSortCondition(sort);

        // 게시글 목록 조회
        Slice<PostListResDto> postSlice = postRepository.findPostList(type, topic, sort, lastPostId, pageSize);
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
        });

        PagingPostListResDto resDto = new PagingPostListResDto(postSlice);

        if (lastPostId == null) {
            resDto.setNoticeBanner(getNoticeBannerInfo());
        }

        // 썸네일용 이미지 반환 (추가 예정)

        return resDto;
    }

    private PagingPostListResDto.NoticeBannerInfo getNoticeBannerInfo() {
        return PagingPostListResDto.NoticeBannerInfo.from(Objects.requireNonNull(noticeRepository.findFirstByOrderByCreatedAtDesc().orElse(null)));
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
    @CheckCommunityAvailable
    public PagingPostSearchListResDto postSearchList(AnimalType type, List<Topic> topic, String sort, Long lastPostId, Integer pageSize, String searchKeyword, String searchScope) {
        validateSortCondition(sort);
        validateSearchScope(searchScope);
        Users currentUser = UserUtil.getCurrentUser();

        if (searchKeyword.length() < 2) {
            throw new CustomException(ErrorCode.INVALID_SEARCH_KEYWORD);
        }

        if (currentUser != null && StringUtils.hasText(searchKeyword)) {
            recentService.saveRecentCommunitySearch(searchKeyword, currentUser);
        }

        PagingPostSearchListResDto pagingResult =
                postRepository.findPostSearchList(type, topic, sort, lastPostId, pageSize, searchKeyword, searchScope);

        List<PostSearchListResDto> posts = pagingResult.getContent();
        if (posts.isEmpty()) {
            return pagingResult;
        }

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
            dto.setCreatedAt(TimeUtil.formatCreatedAt(LocalDateTime.parse(dto.getCreatedAt())));
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

    @Cacheable(
            value = "myPosts",
            key = "#currentUser.id + '_0'",
            condition = "#keyword == null"
    )
    @Transactional(readOnly = true)
    @CheckCommunityAvailable
    public PagingMyPostListResDto myPostList(String keyword, Long lastPostId, Pageable pageable) {
        Users currentUser = UserUtil.getCurrentUser();

        PagingMyPostListResDto postListResDto = postRepository.findMyPostList(currentUser, keyword, lastPostId, pageable);
        List<MyPostListResDto> posts = postListResDto.getContent();

        if (posts.isEmpty()) {
            return postListResDto;
        }

        List<Long> postIds = posts.stream()
                .map(MyPostListResDto::getPostId)
                .toList();

        // Redis에서 조회수 일괄 조회
        Map<Long, Long> viewCountMap = postViewCountRepository.readAll(postIds);

        // DTO 매핑
        posts.forEach(dto -> {
            dto.setViewCount(viewCountMap.getOrDefault(dto.getPostId(), 0L));
        });

        return new PagingMyPostListResDto(posts, postListResDto.isHasNext());
    }

    private Post getPost(Long postId) {
        // 현재 조회하는 게시글
        return postRepository.findById(postId)
                .orElseThrow(() -> new CustomException(ErrorCode.POST_NOT_FOUND));
    }

    private boolean currentUserIsPostAuthor(Users postAuthor, Users currentUser) {
        if (postAuthor == null || currentUser == null) return false;
        return postAuthor.getId().equals(currentUser.getId());
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
