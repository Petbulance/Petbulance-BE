package com.example.Petbulance_BE.domain.report.service;

import com.example.Petbulance_BE.domain.comment.entity.PostComment;
import com.example.Petbulance_BE.domain.comment.repository.PostCommentRepository;
import com.example.Petbulance_BE.domain.comment.service.PostCommentService;
import com.example.Petbulance_BE.domain.post.entity.Post;
import com.example.Petbulance_BE.domain.post.repository.PostRepository;
import com.example.Petbulance_BE.domain.report.dto.request.ReportActionReqDto;
import com.example.Petbulance_BE.domain.report.dto.request.ReportCreateReqDto;
import com.example.Petbulance_BE.domain.report.dto.response.PagingReportListResDto;
import com.example.Petbulance_BE.domain.report.dto.response.ReportActionResDto;
import com.example.Petbulance_BE.domain.report.dto.response.ReportCreateResDto;
import com.example.Petbulance_BE.domain.report.entity.Report;
import com.example.Petbulance_BE.domain.report.repository.ReportRepository;
import com.example.Petbulance_BE.domain.report.type.ReportActionType;
import com.example.Petbulance_BE.domain.report.type.ReportStatus;
import com.example.Petbulance_BE.domain.report.type.ReportType;
import com.example.Petbulance_BE.domain.user.entity.Users;
import com.example.Petbulance_BE.domain.user.repository.UsersJpaRepository;
import com.example.Petbulance_BE.domain.user.type.SactionType;
import com.example.Petbulance_BE.global.common.error.exception.CustomException;
import com.example.Petbulance_BE.global.common.error.exception.ErrorCode;
import com.example.Petbulance_BE.global.util.UserUtil;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class ReportService {
    private final ReportRepository reportRepository;
    private final PostRepository postRepository;
    private final PostCommentRepository postCommentRepository;
    private final UsersJpaRepository usersJpaRepository;
    private final PostCommentService postCommentService;
    private final CommunitySanctionService communitySanctionService;

    public ReportCreateResDto createReport(@Valid ReportCreateReqDto reqDto) {

        Users reporter = UserUtil.getCurrentUser();
        Report report;

        switch (reqDto.getReportType()) {

            case POST -> {
                Post post = postRepository.findById(reqDto.getPostId())
                        .orElseThrow(() -> new CustomException(ErrorCode.POST_NOT_FOUND));

                report = Report.builder()
                        .reportReason(reqDto.getReportReason())
                        .targetUser(post.getUser())
                        .reporter(reporter)
                        .reportType(ReportType.POST)
                        .status(post.isDeleted() ? ReportStatus.DELETED : ReportStatus.REPORTED)
                        .postId(post.getId())
                        .build();
            }

            case COMMENT -> {
                PostComment comment = postCommentRepository.findById(reqDto.getCommentId())
                        .orElseThrow(() -> new CustomException(ErrorCode.COMMENT_NOT_FOUND));

                report = Report.builder()
                        .reportReason(reqDto.getReportReason())
                        .targetUser(comment.getUser())
                        .reporter(reporter)
                        .reportType(ReportType.COMMENT)
                        .status(comment.getDeleted() ? ReportStatus.DELETED : ReportStatus.REPORTED)
                        .commentId(comment.getId())
                        .build();
            }

            default -> throw new IllegalStateException("잘못된 신고 타입입니다.");
        }

        reportRepository.save(report);

        return new ReportCreateResDto("신고가 정상적으로 접수되었습니다.");
    }

    public PagingReportListResDto reportList(int page, int size) {
        log.info("page={}, size={}", page, size);
        return reportRepository.findPagingReports(page, size);
    }

    public ReportActionResDto processReport(Long reportId, ReportActionReqDto reqDto) {
        Report report = reportRepository.findById(reportId).orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND_REPORT));

        switch (reqDto.getActionType()) {
            case PUBLISH -> {
                report.publish();
                return new ReportActionResDto(ReportActionType.PUBLISH, "신고 조치가 처리되었습니다.");
            }
            case SUSPEND -> {
                postAndCommentDelete(report);
                report.deleteAction(ReportActionType.SUSPEND);

                // 커뮤니티 기능 접근 정지
                communitySanctionService.applySanctionForReport(report, SactionType.COMMUNITY_BAN);

                // 알림 보내기
                sendAlarm(report);

                return new ReportActionResDto(ReportActionType.SUSPEND, "신고 조치가 처리되었습니다.");
            }
            case WARNING -> {
                // 해당 게시글, 댓글 삭제
                postAndCommentDelete(report);

                // 알림 보내기
                sendAlarm(report);

                return new ReportActionResDto(ReportActionType.WARNING, "신고 조치가 처리되었습니다.");
            }

            default -> throw new IllegalStateException("잘못된 조치 타입입니다.");
        }
    }

    private void sendAlarm(Report report) {

    }

    private void postAndCommentDelete(Report report) {
        // 해당 게시글, 댓글 삭제
        if(report.getReportType().equals(ReportType.POST)) {
            // 게시글 신고면 해당 게시글 삭제
            postRepository.deleteById(report.getPostId());
        } else if(report.getReportType().equals(ReportType.COMMENT)) {
            // 댓글 신고면 해당 댓글 삭제
            postCommentService.deletePostComment(report.getCommentId());
        }
    }
}
