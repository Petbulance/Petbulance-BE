package com.example.Petbulance_BE.domain.report.service;

import com.example.Petbulance_BE.domain.comment.entity.PostComment;
import com.example.Petbulance_BE.domain.comment.repository.PostCommentRepository;
import com.example.Petbulance_BE.domain.post.entity.Post;
import com.example.Petbulance_BE.domain.post.repository.PostRepository;
import com.example.Petbulance_BE.domain.report.dto.request.ReportActionReqDto;
import com.example.Petbulance_BE.domain.report.dto.request.ReportCreateReqDto;
import com.example.Petbulance_BE.domain.report.dto.response.PagingReportListResDto;
import com.example.Petbulance_BE.domain.report.dto.response.ReportActionResDto;
import com.example.Petbulance_BE.domain.report.dto.response.ReportCreateResDto;
import com.example.Petbulance_BE.domain.report.entity.Report;
import com.example.Petbulance_BE.domain.report.repository.ReportRepository;
import com.example.Petbulance_BE.domain.report.type.ReportStatus;
import com.example.Petbulance_BE.domain.report.type.ReportType;
import com.example.Petbulance_BE.domain.user.entity.Users;
import com.example.Petbulance_BE.domain.user.repository.UsersJpaRepository;
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

    public ReportCreateResDto createReport(@Valid ReportCreateReqDto reqDto) {

        Users reporter = UserUtil.getCurrentUser();
        Report report;

        switch (reqDto.getReportType()) {

            case POST -> {
                Post post = postRepository.findById(reqDto.getPostId())
                        .orElseThrow(() -> new CustomException(ErrorCode.POST_NOT_FOUND));

                report = Report.builder()
                        .reportReason(reqDto.getReportReason())
                        .reporter(reporter)
                        .reportType(ReportType.POST)
                        .status(post.isDeleted() ? ReportStatus.DELETED : ReportStatus.PUBLISHED)
                        .postId(post.getId())
                        .build();
            }

            case COMMENT -> {
                PostComment comment = postCommentRepository.findById(reqDto.getCommentId())
                        .orElseThrow(() -> new CustomException(ErrorCode.COMMENT_NOT_FOUND));

                report = Report.builder()
                        .reportReason(reqDto.getReportReason())
                        .reporter(reporter)
                        .reportType(ReportType.COMMENT)
                        .status(comment.getDeleted() ? ReportStatus.DELETED : ReportStatus.PUBLISHED)
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
        return null;
    }
}
