package com.example.Petbulance_BE.domain.report.service;

import com.example.Petbulance_BE.domain.board.entity.Board;
import com.example.Petbulance_BE.domain.board.repository.BoardRepository;
import com.example.Petbulance_BE.domain.post.entity.Post;
import com.example.Petbulance_BE.domain.post.repository.PostRepository;
import com.example.Petbulance_BE.domain.post.type.Category;
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
import com.example.Petbulance_BE.global.common.type.Role;
import com.example.Petbulance_BE.global.util.JWTUtil;
import com.example.Petbulance_BE.global.util.UserUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
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

    private Users reporter;
    private Users targetUser;
    private Post post;
    private Board board;

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

        board = boardRepository.save(
                Board.builder()
                        .description("설명")
                        .nameEn("영어이름")
                        .nameKr("한글이름")
                        .build()
        );

        post = postRepository.save(
                Post.builder()
                        .user(targetUser)
                        .title("문제 있는 게시글")
                        .content("욕설 포함")
                        .category(Category.DAILY)
                        .imageNum(0)
                        .board(board)
                        .build()
        );


    }

    @Test
    void 신고가_정상적으로_생성된다() {

        // given
        UserUtil.setCurrentUser(reporter); // 테스트용 유틸 필요

        ReportCreateReqDto reqDto = new ReportCreateReqDto(
                ReportType.POST,
                "욕설 신고",
                post.getId(),
                null,
                null
        );

        // when
        reportService.createReport(reqDto);

        // then
        Report report = reportRepository.findAll().get(0);

        assertThat(report.getTargetUser()).isEqualTo(targetUser);
        assertThat(report.getReporter()).isEqualTo(reporter);
    }

    @Test
    void 신고처리_SUSPEND_시_커뮤니티가_정지된다() {

        // given
        UserUtil.setCurrentUser(reporter);

        Report report = reportRepository.save(
                Report.builder()
                        .reportReason("욕설")
                        .reportType(ReportType.POST)
                        .reporter(reporter)
                        .targetUser(targetUser)
                        .status(ReportStatus.PENDING)
                        .postId(post.getId())
                        .build()
        );

        ReportActionReqDto actionReqDto =
                new ReportActionReqDto(ReportActionType.SUSPEND);

        // when
        reportService.processReport(report.getReportId(), actionReqDto);

        // then
        Users bannedUser = usersJpaRepository.findById(targetUser.getId()).orElseThrow();

        assertThat(bannedUser.isCommunityBanned()).isTrue();
    }

    @Test
    void 정지된_유저는_커뮤니티_API_접근시_403_응답을_받는다() throws Exception {

        // given
        targetUser.banCommunityUntil(LocalDateTime.now().plusDays(7));

        String token = jwtUtil.createJwt(
                targetUser.getId(),
                "ACCESS",
                targetUser.getRole().name(),
                "TEST"
        );

        // when & then
        mockMvc.perform(
                        get("/posts")
                                .header("Authorization", "Bearer " + token)
                )
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.error").value("COMMUNITY_BANNED"));
    }




}