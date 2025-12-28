package com.example.Petbulance_BE.domain.report.dummy;

import com.example.Petbulance_BE.domain.board.entity.Board;
import com.example.Petbulance_BE.domain.board.repository.BoardRepository;
import com.example.Petbulance_BE.domain.post.entity.Post;
import com.example.Petbulance_BE.domain.post.repository.PostRepository;
import com.example.Petbulance_BE.domain.post.type.Category;
import com.example.Petbulance_BE.domain.comment.entity.PostComment;
import com.example.Petbulance_BE.domain.comment.repository.PostCommentRepository;
import com.example.Petbulance_BE.domain.report.entity.Report;
import com.example.Petbulance_BE.domain.report.repository.ReportRepository;
import com.example.Petbulance_BE.domain.report.type.ReportActionType;
import com.example.Petbulance_BE.domain.report.type.ReportStatus;
import com.example.Petbulance_BE.domain.report.type.ReportType;
import com.example.Petbulance_BE.domain.user.entity.Users;
import com.example.Petbulance_BE.domain.user.repository.UsersJpaRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

import java.util.*;

@Slf4j
//@Component
@RequiredArgsConstructor
public class ReportInitializer implements ApplicationRunner {

    private final PostRepository postRepository;
    private final PostCommentRepository postCommentRepository;
    private final ReportRepository reportRepository;
    private final UsersJpaRepository userRepository;
    private final BoardRepository boardRepository;

    private final Random random = new Random();

    @Override
    public void run(ApplicationArguments args) {

        log.info("🔹 Dummy ReportInitializer 시작");

        // === 유저 (최소 2명 있다고 가정) ===
        Users user1 = userRepository.findById("user-000001").orElseThrow();
        Users user2 = userRepository.findById("user-000002").orElseThrow();

        List<Users> users = List.of(user1, user2);

        // === 보드 ===
        List<Board> boards = boardRepository.findAll();

        if (boards.isEmpty()) {
            log.warn("⚠ boards 데이터가 없습니다 — 먼저 boards 더미를 넣으세요");
            return;
        }

        List<Post> posts = new ArrayList<>();
        List<PostComment> comments = new ArrayList<>();

        // ================================
        // ⭐ 1) 게시글 20개 생성
        // ================================
        for (int i = 1; i <= 20; i++) {
            Users writer = users.get(random.nextInt(users.size()));
            Board board = boards.get(random.nextInt(boards.size()));
            Category category = Category.values()[random.nextInt(Category.values().length)];

            Post post = Post.builder()
                    .user(writer)
                    .board(board)
                    .category(category)
                    .title("테스트 게시글 #" + i)
                    .content("테스트 게시글 내용입니다. index = " + i)
                    .imageNum(random.nextInt(3))
                    .build();

            posts.add(postRepository.save(post));
        }

        log.info("✅ 게시글 20개 생성 완료");

        // ================================
        // ⭐ 2) 댓글 20개 생성
        // ================================
        for (int i = 1; i <= 20; i++) {

            Post targetPost = posts.get(random.nextInt(posts.size()));
            Users writer = users.get(random.nextInt(users.size()));

            PostComment comment = PostComment.builder()
                    .post(targetPost)
                    .user(writer)
                    .content("테스트 댓글 #" + i)
                    .isSecret(false)
                    .isCommentFromPostAuthor(
                            targetPost.getUser().getId().equals(writer.getId())
                    )
                    .build();

            comments.add(postCommentRepository.save(comment));
        }

        log.info("✅ 댓글 20개 생성 완료");

        // ================================
        // ⭐ 3) 신고 생성 (랜덤)
        // ================================

        // 신고 이유 후보
        List<String> reasons = List.of(
                "욕설 포함",
                "광고성 게시글",
                "커뮤니티 규칙 위반",
                "허위 정보 의심",
                "도배 행위",
                "개인정보 노출"
        );

        // 게시글 신고 10개
        for (int i = 0; i < 10; i++) {
            Post target = posts.get(random.nextInt(posts.size()));
            Users reporter = users.get(random.nextInt(users.size()));

            reportRepository.save(
                    Report.builder()
                            .reportReason(reasons.get(random.nextInt(reasons.size())))
                            .reporter(reporter)
                            .reportType(ReportType.POST)
                            .status(ReportStatus.PUBLISHED)
                            .actionType(randomAction())
                            .postId(target.getId())
                            .build()
            );
        }

        // 댓글 신고 10개
        for (int i = 0; i < 10; i++) {
            PostComment target = comments.get(random.nextInt(comments.size()));
            Users reporter = users.get(random.nextInt(users.size()));

            reportRepository.save(
                    Report.builder()
                            .reportReason(reasons.get(random.nextInt(reasons.size())))
                            .reporter(reporter)
                            .reportType(ReportType.COMMENT)
                            .status(ReportStatus.PUBLISHED)
                            .actionType(randomAction())
                            .commentId(target.getId())
                            .build()
            );
        }

        log.info("✅ 신고 데이터 생성 완료");
        log.info("🎉 Dummy 데이터 세팅 완료");
    }

    private ReportActionType randomAction() {
        ReportActionType[] actions = ReportActionType.values();
        return actions[random.nextInt(actions.length)];
    }
}
