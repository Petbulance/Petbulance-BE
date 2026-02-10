package com.example.Petbulance_BE.domain.admin.review.dto;

import com.example.Petbulance_BE.domain.admin.page.PageResponse;
import com.example.Petbulance_BE.domain.report.entity.Report;
import com.example.Petbulance_BE.domain.report.type.ReportActionType;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AdminDetailReviewResDto {

    private String hospitalName;

    private String address;

    private String roadAddress;

    private Double latitude;

    private Double longitude;

    private String treatmentService;

    private List<String> images;

    private String title;

    private Long totalPrice;

    private Double facilityRating;

    private Double expertiseRating;

    private Double kindnessRating;

    private String reviewContent;

    PageResponse<ReviewHistoryResDto> history;

    @Setter
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class ReviewHistoryResDto {

        public ReviewHistoryResDto(Report report){

            this.date = report.getCreatedAt();
            this.description = report.getReportReason();
            this.reportActionType = report.getActionType();
            this.reporterId = report.getReporter().getId();

        }

        @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
        private LocalDateTime date;

        private String description;

        private ReportActionType reportActionType;

        private String reporterId;

    }

}
