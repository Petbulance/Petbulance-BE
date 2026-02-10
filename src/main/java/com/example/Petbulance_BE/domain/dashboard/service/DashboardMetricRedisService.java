package com.example.Petbulance_BE.domain.dashboard.service;

import com.example.Petbulance_BE.domain.dashboard.type.VisitType;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
public class DashboardMetricRedisService {

    private final StringRedisTemplate redisTemplate;
    private static final long VISIT_TTL_DAYS = 1;

    public void incrementTodaySignup() {
        String key = "dashboard:" + LocalDate.now();
        redisTemplate.opsForHash().increment(key, "signup_count", 1);
        redisTemplate.expire(key, 2, TimeUnit.DAYS);
    }

    public int getSignupCount(LocalDate date) {
        String key = "dashboard:" + date;
        Object value = redisTemplate.opsForHash().get(key, "signup_count");
        return value == null ? 0 : Integer.parseInt(value.toString());
    }

    public void incrementTodayHospitalSearch() {
        String key = "dashboard:" + LocalDate.now();
        redisTemplate.opsForHash().increment(key, "hospital_search_count", 1);
        redisTemplate.expire(key, 2, TimeUnit.DAYS);
    }

    public int getHospitalSearchCount(LocalDate date) {
        String key = "dashboard:" + date;
        Object value = redisTemplate.opsForHash().get(key, "hospital_search_count");
        return value == null ? 0 : Integer.parseInt(value.toString());
    }

    public void incrementTodayReviewCreated() {
        String key = "dashboard:" + LocalDate.now();
        redisTemplate.opsForHash().increment(key, "review_created_count", 1);
        redisTemplate.expire(key, 2, TimeUnit.DAYS);
    }

    public int getReviewCreatedCount(LocalDate date) {
        String key = "dashboard:" + date;
        Object value = redisTemplate.opsForHash().get(key, "review_created_count");
        return value == null ? 0 : Integer.parseInt(value.toString());
    }

    public void incrementTodayPostCreated() {
        String key = "dashboard:" + LocalDate.now();
        redisTemplate.opsForHash().increment(key, "post_created_count", 1);
        redisTemplate.expire(key, 2, TimeUnit.DAYS);
    }

    public int getPostCreatedCount(LocalDate date) {
        String key = "dashboard:" + date;
        Object value = redisTemplate.opsForHash().get(key, "post_created_count");
        return value == null ? 0 : Integer.parseInt(value.toString());
    }

    /* ================= 방문 이벤트 ================= */

    public void incrementTodayVisit(VisitType visitType) {
        String key = getTodayVisitKey();
        redisTemplate.opsForHash().increment(key, visitType.name(), 1);
        redisTemplate.expire(key, VISIT_TTL_DAYS, TimeUnit.DAYS);
    }

    public int getVisitCount(LocalDate date, VisitType visitType) {
        String key = getVisitKey(date);
        Object value = redisTemplate.opsForHash().get(key, visitType.name());
        return value == null ? 0 : Integer.parseInt(value.toString());
    }

    /* ================= key utils ================= */

    private String getTodayVisitKey() {
        return getVisitKey(LocalDate.now());
    }

    private String getVisitKey(LocalDate date) {
        return "dashboard:visit:" + date;
    }


}
