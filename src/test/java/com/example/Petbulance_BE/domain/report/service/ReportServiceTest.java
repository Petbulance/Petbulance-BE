package com.example.Petbulance_BE.domain.report.service;

import com.example.Petbulance_BE.domain.board.entity.Board;
import com.example.Petbulance_BE.domain.board.repository.BoardRepository;
import com.example.Petbulance_BE.domain.comment.entity.PostComment;
import com.example.Petbulance_BE.domain.comment.repository.PostCommentRepository;
import com.example.Petbulance_BE.domain.post.entity.Post;
import com.example.Petbulance_BE.domain.post.repository.PostRepository;
import com.example.Petbulance_BE.domain.post.service.PostService;
import com.example.Petbulance_BE.domain.post.type.Topic;
import com.example.Petbulance_BE.domain.report.dto.request.ReportActionReqDto;
import com.example.Petbulance_BE.domain.report.dto.request.ReportCreateReqDto;
import com.example.Petbulance_BE.domain.report.entity.Report;
import com.example.Petbulance_BE.domain.report.exception.CommunityBannedException;
import com.example.Petbulance_BE.domain.report.repository.ReportRepository;
import com.example.Petbulance_BE.domain.report.type.ReportActionType;
import com.example.Petbulance_BE.domain.report.type.ReportStatus;
import com.example.Petbulance_BE.domain.report.type.ReportType;
import com.example.Petbulance_BE.domain.user.entity.Users;
import com.example.Petbulance_BE.domain.user.repository.UsersJpaRepository;
import com.example.Petbulance_BE.global.common.error.exception.CustomException;
import com.example.Petbulance_BE.global.common.error.exception.ErrorCode;
import com.example.Petbulance_BE.global.common.type.AnimalType;
import com.example.Petbulance_BE.global.common.type.Role;
import com.example.Petbulance_BE.global.util.JWTUtil;
import com.example.Petbulance_BE.global.util.UserUtil;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
@ActiveProfiles("test")
class ReportServiceTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UsersJpaRepository usersJpaRepository;

    @Autowired
    private PostRepository postRepository;

    @Autowired
    private ReportRepository reportRepository;

    @Autowired
    private ReportService reportService;

    @Autowired
    private BoardRepository boardRepository;

    @Autowired
    private JWTUtil jwtUtil;

    @Autowired
    private PostCommentRepository postCommentRepository;

    @Autowired
    private PostService postService;

    @Autowired
    private CommunitySanctionService communitySanctionService;

    @Autowired
    private EntityManager em; // 엔티티 매니저 주입

    private Users reporter;
    private Users targetUser;
    private Post post;
    private PostComment comment;

    @BeforeEach
    void setUp() {
        reporter = usersJpaRepository.save(
                Users.builder()
                        .nickname("신고자")
                        .role(Role.ROLE_CLIENT)
                        .build()
        );

        targetUser = usersJpaRepository.save(
                Users.builder()
                        .nickname("신고당한유저")
                        .role(Role.ROLE_CLIENT)
                        .build()
        );

        post = postRepository.save(
                Post.builder()
                        .user(targetUser)
                        .title("문제 있는 게시글")
                        .content("욕설 포함")
                        .topic(Topic.DAILY)
                        .animalType(AnimalType.AMPHIBIAN)
                        .imageNum(0)
                        .build()
        );

        comment = postCommentRepository.save(
                PostComment.builder()
                        .post(post)
                        .user(targetUser)
                        .content("댓글 내용")
                        .parent(null)
                        .isSecret(false)
                        .build()
        );
    }

    @Test
    @DisplayName("신고 처리 시 경고 3회 누적 시 이용 정지 확인")
    void processReport_IncreaseWarningAndSuspendUser() {
        // [1] Given: 이미 경고가 2회인 상태 설정
        UserUtil.setCurrentUser(reporter);

        targetUser.increaseWarningCount();
        targetUser.increaseWarningCount();
        usersJpaRepository.saveAndFlush(targetUser);

        Report report = saveTestReport(reporter, targetUser, post.getId());

        em.flush();
        em.clear();

        reportService.processReport(report.getReportId(), new ReportActionReqDto(ReportActionType.WARNING));
        em.flush();
        em.clear();

        Users updatedUser = usersJpaRepository.findById(targetUser.getId())
                .orElseThrow(() -> new RuntimeException("유저를 찾을 수 없습니다."));

        // 경고 횟수 및 정지 날짜 확인
        assertThat(updatedUser.getWarningCount()).isEqualTo(3);
        assertThat(updatedUser.getCommunityBanUntil()).isNotNull();
        assertThat(updatedUser.getCommunityBanUntil()).isAfter(LocalDateTime.now());

        // 게시글 삭제 확인
        Optional<Post> deletedPost = postRepository.findById(post.getId());
        assertThat(deletedPost).isEmpty();
    }

    @Test
    @DisplayName("이미 정지 상태인 사용자가 추가 제재를 받으면 정지 기간이 연장된다.")
    void processReport_ExtendSuspensionPeriod() {
        UserUtil.setCurrentUser(reporter);
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime existingBan = now.plusDays(4);

        targetUser.banCommunityUntil(existingBan);
        usersJpaRepository.saveAndFlush(targetUser);

        Report report = saveTestReport(reporter, targetUser, post.getId());

        em.flush();
        em.clear();

        reportService.processReport(report.getReportId(), new ReportActionReqDto(ReportActionType.SUSPEND));

        em.flush();
        em.clear();

        // Then: 검증
        Users updatedUser = usersJpaRepository.findById(targetUser.getId()).orElseThrow();

        // 정지 기간이 연장되었는지 확인 (기본 정지 기간이 7일이라면, 현재 시점 + 7일이어야 함)
        assertThat(updatedUser.getCommunityBanUntil()).isAfter(existingBan);
        assertThat(updatedUser.getCommunityBanUntil()).isAfterOrEqualTo(now.plusDays(7).minusSeconds(1));
    }

    @Test
    @DisplayName("게시글 신고 5회 누적 시 자동 블라인드(hidden=true) 확인")
    void report_AutoBlindAfter5Reports() {
        UserUtil.setCurrentUser(reporter);

        setReportCount(post, 4);
        postRepository.saveAndFlush(post);

        ReportCreateReqDto reqDto = new ReportCreateReqDto(ReportType.POST, "5번째 신고", post.getId(), null, null);

        em.flush();
        em.clear();

        reportService.createReport(reqDto);

        em.flush();
        em.clear();

        Post updatedPost = postRepository.findById(post.getId())
                .orElseThrow(() -> new AssertionError("게시글을 찾을 수 없습니다."));

        assertThat(updatedPost.getReportCount()).isEqualTo(5);
        assertThat(updatedPost.isHidden()).isTrue();
    }

    @Test
    @DisplayName("블라인드 처리된 게시글 상세 조회 시 예외 발생 확인")
    void detailPost_ThrowExceptionWhenHidden() {
        UserUtil.setCurrentUser(reporter);

        // given
        post.updateHidden();
        postRepository.save(post);

        // when & then
        assertThatThrownBy(() -> postService.detailPost(post.getId()))
                .isInstanceOf(CustomException.class)
                .satisfies(ex -> {
                    CustomException exception = (CustomException) ex;
                    assertThat(exception.getErrorCode()).isEqualTo(ErrorCode.POST_HIDDEN);
                });
    }

    @Test
    @DisplayName("이용 정지당한 사용자가 상세 조회 API 호출 시 CommunityBannedException 발생 확인")
    void detailPost_ThrowExceptionWhenUserBanned() {
        // given: 사용자를 정지 상태로 설정
        LocalDateTime banUntil = LocalDateTime.now().plusDays(7);
        targetUser.banCommunityUntil(banUntil);
        usersJpaRepository.save(targetUser);

        // 현재 사용자 설정
        UserUtil.setCurrentUser(targetUser);

        // when & then: @CheckCommunityAvailable AOP가 작동한다고 가정
        assertThatThrownBy(() -> postService.detailPost(post.getId()))
                .isInstanceOf(CommunityBannedException.class)
                .satisfies(ex -> {
                    CommunityBannedException exception = (CommunityBannedException) ex;
                    assertThat(exception.getBannedUntil()).isEqualTo(banUntil);
                });
    }

    @Test
    @DisplayName("관리자가 조치 완료 시 신고 상태 변경 및 게시글 삭제 확인")
    void processReport_CompleteAndRemoveContent() {
        // [1] given
        UserUtil.setCurrentUser(reporter);
        Report report = saveTestReport(reporter, targetUser, post.getId());

        em.flush();
        em.clear();

        reportService.processReport(report.getReportId(), new ReportActionReqDto(ReportActionType.WARNING));

        em.flush();
        em.clear();

        Report updatedReport = reportRepository.findById(report.getReportId()).orElseThrow();
        assertThat(updatedReport.getStatus()).isEqualTo(ReportStatus.COMPLETED);

        Optional<Post> deletedPost = postRepository.findById(post.getId());
        assertThat(deletedPost).isEmpty();
    }

    private Report saveTestReport(Users reporter, Users targetUser, Long postId) {
        return reportRepository.save(Report.builder()
                .reportReason("부적절한 콘텐츠")
                .reporter(reporter)
                .targetUser(targetUser)
                .reportType(ReportType.POST)
                .status(ReportStatus.PENDING)
                .postId(postId)
                .createdAt(LocalDateTime.now())
                .build());
    }

    private void setReportCount(Post post, int count) {
        for (int i = 0; i < count; i++) {
            post.increaseReportCount();
        }
        postRepository.save(post);
    }

    @Test
    @DisplayName("관리자가 정지 처리 시 콘텐츠가 삭제되고, 7일 후에는 이용 정지가 해제된다.")
    void processReport_SuspendAndAutoUnbanAfter7Days() {
        // 1. Given: 인증 정보 설정 및 데이터 준비
        UserUtil.setCurrentUser(reporter);

        setReportCount(post, 5);
        post.updateHidden();
        postRepository.saveAndFlush(post);

        Report report = saveTestReport(reporter, targetUser, post.getId());

        em.flush();
        em.clear();

        // 2. When: 관리자가 정지(SUSPEND) 처리
        reportService.processReport(report.getReportId(), new ReportActionReqDto(ReportActionType.SUSPEND));

        em.flush();
        em.clear();

        Report updatedReport = reportRepository.findById(report.getReportId())
                .orElseThrow(() -> new AssertionError("신고를 찾을 수 없습니다."));

        assertThat(updatedReport.getStatus()).isEqualTo(ReportStatus.COMPLETED);
        assertThat(updatedReport.getActionType()).isEqualTo(ReportActionType.SUSPEND);

        Users bannedUser = usersJpaRepository.findById(targetUser.getId()).orElseThrow();
        assertThat(bannedUser.getCommunityBanUntil()).isNotNull();
        assertThat(bannedUser.getCommunityBanUntil()).isAfter(LocalDateTime.now());

        // 게시글 삭제 확인
        assertThat(postRepository.findById(post.getId())).isEmpty();

        bannedUser.banCommunityUntil(LocalDateTime.now().minusMinutes(1));
        usersJpaRepository.saveAndFlush(bannedUser);
        communitySanctionService.checkCommunityAccess(bannedUser);
        assertThat(bannedUser.getCommunityBanUntil()).isNull();
    }

    @Test
    @DisplayName("이미 완료된 신고 건에 대해 다시 처리를 시도하면 예외가 발생한다.")
    void processReport_AlreadyCompleted_ThrowException() {
        UserUtil.setCurrentUser(reporter);
        Report report = saveTestReport(reporter, targetUser, post.getId());
        Long reportId = report.getReportId();

        report.deleteAction(ReportActionType.WARNING);
        reportRepository.saveAndFlush(report);

        em.clear();

        assertThatThrownBy(() -> reportService.processReport(reportId, new ReportActionReqDto(ReportActionType.SUSPEND)))
                .isInstanceOf(CustomException.class)
                .satisfies(ex -> {
                    CustomException exception = (CustomException) ex;
                    assertThat(exception.getErrorCode()).isEqualTo(ErrorCode.ALREADY_COMPLETED);
                });
    }

    @Test
    @DisplayName("신고된 게시글이 이미 삭제된 상태에서 삭제 처리를 시도하면 예외가 발생한다.")
    void processReport_AlreadyDeletedContent_HandleGracefully() {
        UserUtil.setCurrentUser(reporter);

        Report report = Report.builder()
                .reportReason("부적절한 콘텐츠")
                .reporter(reporter)
                .targetUser(targetUser)
                .reportType(ReportType.POST)
                .status(ReportStatus.PENDING)
                .postId(9999L)
                .build();
        reportRepository.saveAndFlush(report);

        em.clear();

        assertThatThrownBy(() -> reportService.processReport(report.getReportId(), new ReportActionReqDto(ReportActionType.WARNING)))
                .isInstanceOf(CustomException.class)
                .satisfies(ex -> {
                    CustomException exception = (CustomException) ex;
                    assertThat(exception.getErrorCode()).isEqualTo(ErrorCode.POST_NOT_FOUND);
                });
    }

}