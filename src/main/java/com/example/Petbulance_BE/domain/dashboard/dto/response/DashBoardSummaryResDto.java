package com.example.Petbulance_BE.domain.dashboard.dto.response;

import com.example.Petbulance_BE.domain.dashboard.type.ChangeType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class DashBoardSummaryResDto {

    private SignUpResDto signUp; // 서비스 가입자
    private HospitalSearchResDto hospitalSearch; // 병원 검색 횟수
    private ReviewResDto review; // 후기 작성 수
    private PostResDto post; // 게시글 작성 수
    private VisitResDto visit; // 핵심 기능 방문 현황 (금일)
    private CommunityReportDto communityReport; // 커뮤니티 신고
    private QnaDto qna; // 고갟센터 문의
    private ReviewReportDto reviewReport;

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
        private int pendingCount;
    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class ReviewReportDto {
        private int totalCount;
        private int pendingCount;
    }
}

