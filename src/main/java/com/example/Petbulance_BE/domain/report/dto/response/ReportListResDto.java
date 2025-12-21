package com.example.Petbulance_BE.domain.report.dto.response;

import com.example.Petbulance_BE.domain.qna.type.QnaStatus;
import com.example.Petbulance_BE.domain.report.type.ReportActionType;
import com.example.Petbulance_BE.domain.report.type.ReportStatus;
import com.example.Petbulance_BE.domain.report.type.ReportType;
import com.example.Petbulance_BE.global.util.TimeUtil;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ReportListResDto {
    private Long reportId;
    private ReportType reportType;
    private String content;
    private Long postId;
    private Long commentId;
    private String reportReason;
    private String reporterNickname;
    private String targetUserNickname;
    private ReportStatus status;
    private ReportActionType actionType;
    private String createdAt;

    public ReportListResDto(Long reportId, String reportType, String content,
                            Long postId, Long commentId, String reportReason,
                            String reporterNickname, String targetUserNickname,
                            ReportStatus status, ReportActionType actionType,
                            LocalDateTime createdAt) {
        this.reportId = reportId;
        this.reportType = ReportType.valueOf(reportType);;
        this.content = content;
        this.postId = postId;
        this.commentId = commentId;
        this.reportReason = reportReason;
        this.reporterNickname = reporterNickname;
        this.targetUserNickname = targetUserNickname;
        this.status = status;
        this.actionType = actionType;
        this.createdAt = TimeUtil.formatCreatedAt(createdAt);
    }


}
