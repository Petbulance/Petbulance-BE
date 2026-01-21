package com.example.Petbulance_BE.domain.dashboard.dto.response;

import com.example.Petbulance_BE.domain.dashboard.type.ChangeType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class DashBoardSummaryResDto {

    private SignUpResDto signUp;
    private HospitalSearchResDto hospitalSearch;
    private ReviewResDto review;
    private PostResDto post;
    private VisitResDto visit;
    private CommunityReportDto communityReport;
    private QnaDto qna;

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class SignUpResDto {

        private int todaySignupCount;
        private double signupChangeRate; // %
        private ChangeType signupTrend;  // UP / DOWN / SAME
    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class HospitalSearchResDto {
        private int todayHospitalSearchCount;
        private double hospitalSearchChangeRate;
        private ChangeType hospitalSearchTrend;
    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class ReviewResDto {
        private int todayReviewCount;
        private double reviewChangeRate;
        private ChangeType reviewTrend;
    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class PostResDto {
        private int todayPostCount;
        private double postChangeRate;
        private ChangeType postTrend;
    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class VisitResDto {
        private int hospitalSearchVisitCount;
        private int reviewWriteVisitCount;
        private int communityVisitCount;
    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class CommunityReportDto {
        private int totalCount;
        private int pendingCount;
    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class QnaDto {
        private int totalCount;
        private int waitingCount;
    }
}

