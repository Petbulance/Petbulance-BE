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

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class PostDto {
        private String title;
        private String writerNickname;
        private String createdAt;

        public PostDto(String title, String writerNickname, LocalDateTime localDateTime) {
            this.title = title;
            this.writerNickname = writerNickname;
            this.createdAt = TimeUtil.formatCreatedAt(localDateTime);
        }
    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class CommentDto {
        private String content;
        private String writerNickname;
        private String createdAt;

        public CommentDto(String content, String writerNickname, LocalDateTime localDateTime) {
            this.content = content;
            this.writerNickname = writerNickname;
            this.createdAt = TimeUtil.formatCreatedAt(localDateTime);
        }
    }

    public ReportListResDto(Long reportId, ReportType reportType, PostDto post, CommentDto comment, LocalDateTime reportedAt, ReportStatus status, ReportActionType actionType) {
        this.reportId = reportId;
        this.reportType = reportType;
        this.post = post;
        this.comment = comment;
        this.reportedAt = TimeUtil.formatCreatedAt(reportedAt);;
        this.status = status;
        this.actionType = actionType;
    }
}

