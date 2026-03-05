package com.example.Petbulance_BE.domain.notification.dummy;

import com.example.Petbulance_BE.domain.comment.entity.PostComment;
import com.example.Petbulance_BE.domain.comment.repository.PostCommentRepository;
import com.example.Petbulance_BE.domain.notification.entity.Notification;
import com.example.Petbulance_BE.domain.notification.repository.NotificationRepository;
import com.example.Petbulance_BE.domain.notification.type.NotificationTargetType;
import com.example.Petbulance_BE.domain.notification.type.NotificationType;
import com.example.Petbulance_BE.domain.post.entity.Post;
import com.example.Petbulance_BE.domain.post.repository.PostRepository;
import com.example.Petbulance_BE.domain.post.type.Topic;
import com.example.Petbulance_BE.domain.review.entity.UserReview;
import com.example.Petbulance_BE.domain.review.repository.ReviewJpaRepository;
import com.example.Petbulance_BE.domain.user.entity.Users;
import com.example.Petbulance_BE.domain.user.repository.UsersJpaRepository;
import com.example.Petbulance_BE.global.common.type.AnimalType;
import com.example.Petbulance_BE.global.common.type.Role;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;

@Component
@RequiredArgsConstructor
@Profile("local")
public class NotificationDataInitializer implements CommandLineRunner {

    private final UsersJpaRepository userRepository;
    private final PostRepository postRepository;
    private final PostCommentRepository postCommentRepository;
    private final ReviewJpaRepository userReviewRepository;
    private final NotificationRepository notificationRepository;

    @Override
    @Transactional
    public void run(String... args) {
        // 중복 생성 방지
        if (notificationRepository.count() > 0) return;

        // 1. 유저 준비 (수신자: 관리자, 발신자: 일반유저)
        Users owner = userRepository.findFirstByRole(Role.ROLE_ADMIN)
                .orElseThrow(() -> new RuntimeException("DB에 관리자 계정이 존재하지 않습니다."));

        Users actor = Users.builder()
                .nickname("열혈활동가")
                .role(Role.ROLE_CLIENT)
                .build();
        userRepository.save(actor);

        // 2. 타겟 엔티티 생성 (Post, Comment, Review)
        Post post = Post.builder()
                .user(owner).title("더미 게시글").content("내용").topic(Topic.DAILY)
                .animalType(AnimalType.SMALLMAMMALS).imageNum(0).build();
        postRepository.save(post);

        PostComment parentComment = PostComment.builder()
                .post(post).user(owner).content("내 원본 댓글").isSecret(false).build();
        postCommentRepository.save(parentComment);

        UserReview review = UserReview.builder()
                .user(owner).title("병원 후기").reviewContent("좋아요").visitDate(LocalDate.now())
                .animalType(AnimalType.SMALLMAMMALS).build();
        userReviewRepository.save(review);

        // 3. 알림 20개 생성 (유형별 5개씩)
        for (int i = 1; i <= 5; i++) {

            // ✅ 1. 내 글에 댓글 (POST_COMMENT)
            notificationRepository.save(Notification.builder()
                    .receiver(owner).actor(actor)
                    .type(NotificationType.POST_COMMENT)
                    .targetType(NotificationTargetType.POST)
                    .targetId(post.getId())
                    .message(i + "번째 댓글: " + actor.getNickname() + "님이 게시글에 댓글을 남겼습니다.")
                    .read(false).build());

            // ✅ 2. 내 댓글에 답글 (COMMENT_REPLY)
            notificationRepository.save(Notification.builder()
                    .receiver(owner).actor(actor)
                    .type(NotificationType.COMMENT_REPLY)
                    .targetType(NotificationTargetType.COMMENT)
                    .targetId(parentComment.getId())
                    .message(i + "번째 답글: " + actor.getNickname() + "님이 댓글에 답글을 남겼습니다.")
                    .read(false).build());

            // ✅ 3. 내 글 좋아요 (POST_LIKE)
            notificationRepository.save(Notification.builder()
                    .receiver(owner).actor(actor)
                    .type(NotificationType.POST_LIKE)
                    .targetType(NotificationTargetType.POST)
                    .targetId(post.getId())
                    .message(i + "번째 좋아요: " + actor.getNickname() + "님이 게시글을 좋아합니다.")
                    .read(false).build());

            // ✅ 4. 후기 도움돼요 (REVIEW_HELPFUL)
            notificationRepository.save(Notification.builder()
                    .receiver(owner).actor(actor)
                    .type(NotificationType.REVIEW_HELPFUL)
                    .targetType(NotificationTargetType.REVIEW)
                    .targetId(review.getId())
                    .message(i + "번째 도움: " + actor.getNickname() + "님이 후기가 도움된다고 표시했습니다.")
                    .read(false).build());
        }

        System.out.println("✅ 총 20개의 4유형 알림 데이터 생성 완료!");
    }
}
