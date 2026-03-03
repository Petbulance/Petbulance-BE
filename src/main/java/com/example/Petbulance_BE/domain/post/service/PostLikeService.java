package com.example.Petbulance_BE.domain.post.service;

import com.example.Petbulance_BE.domain.device.entity.Device;
import com.example.Petbulance_BE.domain.device.repository.DeviceJpaRepository;
import com.example.Petbulance_BE.domain.notification.service.NotificationService;
import com.example.Petbulance_BE.domain.notification.type.NotificationTargetType;
import com.example.Petbulance_BE.domain.notification.type.NotificationType;
import com.example.Petbulance_BE.domain.post.dto.PostLikeDto;
import com.example.Petbulance_BE.domain.post.entity.Post;
import com.example.Petbulance_BE.domain.post.entity.PostLike;
import com.example.Petbulance_BE.domain.post.entity.PostLikeCount;
import com.example.Petbulance_BE.domain.post.repository.PostLikeCountRepository;
import com.example.Petbulance_BE.domain.post.repository.PostLikeRepository;
import com.example.Petbulance_BE.domain.post.repository.PostRepository;
import com.example.Petbulance_BE.domain.report.aop.communityBan.CheckCommunityAvailable;
import com.example.Petbulance_BE.domain.user.entity.Users;
import com.example.Petbulance_BE.global.common.error.exception.CustomException;
import com.example.Petbulance_BE.global.common.error.exception.ErrorCode;
import com.example.Petbulance_BE.global.firebase.FcmService;
import com.example.Petbulance_BE.global.util.UserUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class PostLikeService {
    private final PostRepository postRepository;
    private final PostLikeRepository postLikeRepository;
    private final PostLikeCountRepository postLikeCountRepository;
    private final NotificationService notificationService;
    private final DeviceJpaRepository deviceJpaRepository;
    private final FcmService fcmService;

    @Transactional
    @CheckCommunityAvailable
    public PostLikeDto postLike(Long postId) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new CustomException(ErrorCode.POST_NOT_FOUND));

        Users currentUser = UserUtil.getCurrentUser();

        // 이미 좋아요한 경우 예외
        if (postLikeRepository.existsByPostIdAndUser(postId, currentUser)) {
            throw new CustomException(ErrorCode.ALREADY_LIKED);
        }

        // 좋아요 생성
        postLikeRepository.save(PostLike.builder()
                .post(post)
                .user(currentUser)
                .build());

        // 좋아요 수 증가
        int result = postLikeCountRepository.increase(postId);
        if (result == 0) { // 좋아요 수 증가에 실패한 경우 -> 좋아요 내역이 없는 경우
            try {
                postLikeCountRepository.save(
                        PostLikeCount.builder()
                                .postId(postId)
                                .postLikeCount(1L) // 좋아요수를 1로 초기화
                                .build()
                );
            } catch (Exception ignored) {
            }
        }

        // 최신 좋아요 수 조회
        long likeCount = Optional.ofNullable(
                postLikeCountRepository.getCountByPostId(postId)
        ).orElse(1L);

        // 자기자신이 좋아요를 누르지 않은 경우에만
        if (!Objects.equals(currentUser.getId(), post.getUser().getId())) sendLikePushAlram(post, currentUser);

        return PostLikeDto.builder()
                .postId(postId)
                .liked(true)
                .likeCount(likeCount)
                .build();
    }

    private void sendLikePushAlram(Post post, Users currentUser) {
        // 게시글 작성자의 fcm 토큰 조회
        Device device = deviceJpaRepository.findByUserId(post.getUser().getId());

        String message = "“" + post.getTitle() + "” 글에 좋아요가 도착했습니다.";

        if (device != null) {
            String fcmToken = device.getFcm_token();

            // 좋아요 푸시 알림 전송
            Map<String, String> data = new HashMap<>();
            data.put("type", "POST");                // 이동할 페이지 타입
            data.put("targetId", String.valueOf(post.getId())); // 이동할 게시글 ID

            String title = "좋아요 알림";
            fcmService.sendPushNotification(fcmToken, title, message, data);
        }

        // 알림 저장
        notificationService.createNotification(post.getUser(), currentUser, NotificationType.POST_LIKE, NotificationTargetType.POST, post.getId(), message);
    }

    @Transactional
    @CheckCommunityAvailable
    public PostLikeDto postUnlike(Long postId) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new CustomException(ErrorCode.POST_NOT_FOUND));

        Users currentUser = UserUtil.getCurrentUser();

        // 현재 사용자에게 좋아요 내역이 있는지 조회
        PostLike postLike = postLikeRepository.findByPostAndUser(post, currentUser)
                .orElseThrow(() -> new CustomException(ErrorCode.LIKE_NOT_FOUND));

        // 좋아요 삭제
        postLikeRepository.delete(postLike);

        // 좋아요수 감소
        int result = postLikeCountRepository.decrease(postId);
        if (result == 0) {
            // update된 행이 없으면 → 0으로 초기화 (이 게시글에 count row가 없는 경우)
            postLikeCountRepository.save(
                    PostLikeCount.builder()
                            .postId(postId)
                            .postLikeCount(0L)
                            .build()
            );
        }

        long likeCount = Optional.ofNullable(
                postLikeCountRepository.getCountByPostId(postId)
        ).orElse(0L);


        return PostLikeDto.builder()
                .postId(postId)
                .liked(false)
                .likeCount(likeCount)
                .build();
    }
}
