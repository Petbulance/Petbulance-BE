package com.example.Petbulance_BE.domain.dashboard.service;

import com.example.Petbulance_BE.domain.dashboard.dto.MetricResult;
import com.example.Petbulance_BE.domain.dashboard.dto.request.EventVisitReqDto;
import com.example.Petbulance_BE.domain.dashboard.dto.response.DashBoardSummaryResDto;
import com.example.Petbulance_BE.domain.dashboard.dto.response.EventVisitResDto;
import com.example.Petbulance_BE.domain.dashboard.type.ChangeType;
import com.example.Petbulance_BE.domain.dashboard.type.VisitType;
import com.example.Petbulance_BE.domain.qna.repository.QnaRepository;
import com.example.Petbulance_BE.domain.qna.type.QnaStatus;
import com.example.Petbulance_BE.domain.report.repository.ReportRepository;
import com.example.Petbulance_BE.domain.report.type.ReportType;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;

@Service
@RequiredArgsConstructor
public class DashBoardService {

    private final DashboardMetricRedisService redisService;
    private final ReportRepository reportRepository;
    private final QnaRepository qnaRepository;

    public DashBoardSummaryResDto dashBoardSummary() {

        LocalDate today = LocalDate.now();
        LocalDate yesterday = today.minusDays(1);

        MetricResult signUp = calculateMetric(
                redisService.getSignupCount(today),
                redisService.getSignupCount(yesterday)
        );

        MetricResult hospitalSearch = calculateMetric(
                redisService.getHospitalSearchCount(today),
                redisService.getHospitalSearchCount(yesterday)
        );

        MetricResult review = calculateMetric(
                redisService.getReviewCreatedCount(today),
                redisService.getReviewCreatedCount(yesterday)
        );

        MetricResult post = calculateMetric(
                redisService.getPostCreatedCount(today),
                redisService.getPostCreatedCount(yesterday)
        );

        DashBoardSummaryResDto.VisitResDto visit =
                new DashBoardSummaryResDto.VisitResDto(
                        redisService.getVisitCount(today, VisitType.HOSPITAL_SEARCH),
                        redisService.getVisitCount(today, VisitType.REVIEW_WRITE),
                        redisService.getVisitCount(today, VisitType.COMMUNITY)
                );

        // 커뮤니티 신고
        int reportTotal = (int) reportRepository.count();
        int reportPending = reportRepository.countByProcessedFalse();

        // 고객센터 문의
        int qnaTotal = (int) qnaRepository.count();
        int qnaWaiting =
                qnaRepository.countByStatus(QnaStatus.ANSWER_WAITING);

        return new DashBoardSummaryResDto(
                new DashBoardSummaryResDto.SignUpResDto(
                        signUp.getTodayCount(),
                        signUp.getChangeRate(),
                        signUp.getTrend()
                ),
                new DashBoardSummaryResDto.HospitalSearchResDto(
                        hospitalSearch.getTodayCount(),
                        hospitalSearch.getChangeRate(),
                        hospitalSearch.getTrend()
                ),
                new DashBoardSummaryResDto.ReviewResDto(
                        review.getTodayCount(),
                        review.getChangeRate(),
                        review.getTrend()
                ),
                new DashBoardSummaryResDto.PostResDto(
                        post.getTodayCount(),
                        post.getChangeRate(),
                        post.getTrend()
                ),
                visit,
                new DashBoardSummaryResDto.CommunityReportDto(
                        reportTotal,
                        reportPending
                ),
                new DashBoardSummaryResDto.QnaDto(
                        qnaTotal,
                        qnaWaiting
                )
        );
    }

    private MetricResult calculateMetric(int today, int yesterday) {
        double changeRate = calculateChangeRate(today, yesterday);
        ChangeType trend = calculateTrend(today, yesterday);
        return new MetricResult(today, changeRate, trend);
    }

    private double calculateChangeRate(int today, int yesterday) {
        if (yesterday == 0) {
            return today == 0 ? 0.0 : 100.0;
        }
        return Math.round(((double) (today - yesterday) / yesterday) * 1000) / 10.0;
    }

    private ChangeType calculateTrend(int today, int yesterday) {
        if (today > yesterday) return ChangeType.UP;
        if (today < yesterday) return ChangeType.DOWN;
        return ChangeType.SAME;
    }

    public EventVisitResDto eventVisit(EventVisitReqDto reqDto) {
        try {
            redisService.incrementTodayVisit(reqDto.getVisitType());
            return new EventVisitResDto("방문 지표가 정상적으로 수집되었습니다.", reqDto.getVisitType());
        } catch (Exception e) {
            // 방문 이벤트는 실패해도 서비스 흐름에 영향 X
            return new EventVisitResDto("방문 지표가 정상적으로 수집되었습니다.", reqDto.getVisitType());
        }
    }
}
