package com.example.Petbulance_BE.domain.report.dto.request;

import com.example.Petbulance_BE.domain.report.type.ReportType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ReportCreateReqDto {
    @NotNull
    private ReportType reportType;   // POST, COMMENT

    @NotBlank
    private String reportReason;

    // 게시글 신고용
    private Long postId;

    // 댓글 신고용
    private Long commentId;
}
