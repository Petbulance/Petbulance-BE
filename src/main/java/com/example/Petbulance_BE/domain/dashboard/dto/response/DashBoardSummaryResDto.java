package com.example.Petbulance_BE.domain.dashboard.dto.response;

import com.example.Petbulance_BE.domain.dashboard.type.ChangeType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class DashBoardSummaryResDto {
    private TodaySignupDto todaySignup;
    private NewReviewDto newReview;
    private CommunityReportDto communityReport;
    private UnansweredInquiryDto unansweredInquiry;

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public class TodaySignupDto {

        private int count;
        private int changeRate;
        private ChangeType changeType;
    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public class NewReviewDto {

        private int count;
        private int changeRate;
        private ChangeType changeType;
    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public class CommunityReportDto {

        private int count;
        private int newCount;
    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public class UnansweredInquiryDto {

        private int count;
        private int newCount;
    }
}
