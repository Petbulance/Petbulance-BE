package com.example.Petbulance_BE.domain.report.dto.response;

import com.example.Petbulance_BE.domain.report.type.ReportActionType;
import com.example.Petbulance_BE.domain.report.type.ReportStatus;
import com.example.Petbulance_BE.domain.report.type.ReportType;
import com.example.Petbulance_BE.global.util.TimeUtil;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
public class ReportListResDto {

    private Long reportId;
    private ReportType reportType;
    private PostDto post;
    private CommentDto comment;
    private String reportedAt;
    private ReportStatus status;     // PUBLISHED, DELETED
    private ReportActionType actionType;
    private String reportReason;

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class PostDto {
        private Long postId;
        private String title;
        private String writerNickname;
        private String createdAt;
        private int reportCount;

        public PostDto(Long postId, String title, String writerNickname, LocalDateTime localDateTime, int reportCount) {
            this.postId = postId;
            this.title = title;
            this.writerNickname = writerNickname;
            this.createdAt = TimeUtil.formatCreatedAt(localDateTime);
            this.reportCount = reportCount;
        }
    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class CommentDto {
        private Long commentId;
        private String content;
        private String writerNickname;
        private String createdAt;
        private int reportCount;
        private Long postId;

        public CommentDto(Long commentId, String content, String writerNickname, LocalDateTime localDateTime, int reportCount, Long postId) {
            this.commentId = commentId;
            this.content = content;
            this.writerNickname = writerNickname;
            this.createdAt = TimeUtil.formatCreatedAt(localDateTime);
            this.reportCount = reportCount;
            this.postId = postId;
        }
    }

    public ReportListResDto(Long reportId, ReportType reportType, PostDto post, CommentDto comment, LocalDateTime reportedAt, ReportStatus status, ReportActionType actionType, String reportReason) {
        this.reportId = reportId;
        this.reportType = reportType;
        this.post = post;
        this.comment = comment;
        this.reportedAt = TimeUtil.formatCreatedAt(reportedAt);;
        this.status = status;
        this.actionType = actionType;
        this.reportReason = reportReason;
    }
}

