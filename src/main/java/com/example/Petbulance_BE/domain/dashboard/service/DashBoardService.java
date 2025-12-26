package com.example.Petbulance_BE.domain.dashboard.service;

import com.example.Petbulance_BE.domain.dashboard.dto.MetricResult;
import com.example.Petbulance_BE.domain.dashboard.dto.response.DashBoardSummaryResDto;
import com.example.Petbulance_BE.domain.dashboard.type.ChangeType;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;

@Service
@RequiredArgsConstructor
public class DashBoardService {

    private final DashboardMetricRedisService redisService;

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
}
