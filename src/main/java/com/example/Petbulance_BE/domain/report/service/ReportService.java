package com.example.Petbulance_BE.domain.report.service;

import com.example.Petbulance_BE.domain.comment.entity.PostComment;
import com.example.Petbulance_BE.domain.comment.repository.PostCommentRepository;
import com.example.Petbulance_BE.domain.post.entity.Post;
import com.example.Petbulance_BE.domain.post.repository.PostRepository;
import com.example.Petbulance_BE.domain.report.dto.request.ReportCreateReqDto;
import com.example.Petbulance_BE.domain.report.dto.response.ReportCreateResDto;
import com.example.Petbulance_BE.domain.report.entity.CommentReport;
import com.example.Petbulance_BE.domain.report.entity.PostReport;
import com.example.Petbulance_BE.domain.report.entity.Report;
import com.example.Petbulance_BE.domain.report.entity.UserReport;
import com.example.Petbulance_BE.domain.report.repository.ReportRepository;
import com.example.Petbulance_BE.domain.report.type.ReportStatus;
import com.example.Petbulance_BE.domain.user.entity.Users;
import com.example.Petbulance_BE.domain.user.repository.UsersJpaRepository;
import com.example.Petbulance_BE.global.common.error.exception.CustomException;
import com.example.Petbulance_BE.global.common.error.exception.ErrorCode;
import com.example.Petbulance_BE.global.util.UserUtil;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ReportService {
    private final ReportRepository reportRepository;
    private final PostRepository postRepository;
    private final PostCommentRepository postCommentRepository;
    private final UsersJpaRepository usersJpaRepository;

    public ReportCreateResDto createReport(@Valid ReportCreateReqDto reqDto) {
        Report report;
        Users reporter = UserUtil.getCurrentUser(); // 신고자

        switch (reqDto.getReportType()) {

            case POST -> { // 신고 유형이 게시글 유형이라면
                Post post = postRepository.findById(reqDto.getPostId())
                        .orElseThrow(() -> new CustomException(ErrorCode.POST_NOT_FOUND));

                report = createPostReport(reporter, post, reqDto);
            }

            case COMMENT -> {
                PostComment comment = postCommentRepository.findById(reqDto.getCommentId())
                        .orElseThrow(() -> new CustomException(ErrorCode.COMMENT_NOT_FOUND));

                report = createCommentReport(reporter, comment, reqDto);
            }

            case USER -> {
                Users targetUser = usersJpaRepository
                        .findByNickname(reqDto.getTargetUserNickname())
                        .orElseThrow(() -> new CustomException(ErrorCode.NON_EXIST_USER));

                report = createUserReport(reporter, targetUser, reqDto);
            }

            default -> throw new IllegalStateException("잘못된 신고 타입");
        }

        reportRepository.save(report);

        return new ReportCreateResDto("신고가 정상적으로 접수되었습니다");
    }

    private PostReport createPostReport(Users reporter, Post post, ReportCreateReqDto req) {
        return PostReport.builder()
                .reportReason(req.getReportReason())
                .reporter(reporter)
                .targetUser(post.getUser()) // 게시글 작성자가 신고 대상자가 됨
                .post(post)
                .build();
    }

    private CommentReport createCommentReport(Users reporter, PostComment comment, ReportCreateReqDto req) {
        return CommentReport.builder()
                .reportReason(req.getReportReason())
                .reporter(reporter)
                .targetUser(comment.getUser())
                .postComment(comment)
                .build();
    }

    private UserReport createUserReport(Users reporter, Users targetUser, ReportCreateReqDto req) {
        return UserReport.builder()
                .reportReason(req.getReportReason())
                .reporter(reporter)
                .targetUser(targetUser)
                .build();
    }
}
