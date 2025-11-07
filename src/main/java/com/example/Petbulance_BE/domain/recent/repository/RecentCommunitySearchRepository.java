package com.example.Petbulance_BE.domain.recent.repository;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Repository;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Set;

@Repository
@RequiredArgsConstructor
public class RecentCommunitySearchRepository {
    private final StringRedisTemplate redisTemplate;
    private static final String PREFIX = "recent_search:";
    private static final long TTL_DAYS = 30;

    public void saveKeyword(String userId, String keyword) {
        String key = PREFIX + userId;
        double score = System.currentTimeMillis();
        redisTemplate.opsForZSet().add(key, keyword, score);
        redisTemplate.expire(key, Duration.ofDays(TTL_DAYS));
    }

    public List<String> findRecentKeywords(String userId, int limit) {
        String key = PREFIX + userId;
        Set<String> results = redisTemplate.opsForZSet()
                .reverseRange(key, 0, limit - 1);
        return new ArrayList<>(Objects.requireNonNullElse(results, Set.of()));
    }

    public void deleteKeyword(String userId, String keyword) {
        String key = PREFIX + userId;
        redisTemplate.opsForZSet().remove(key, keyword);
    }

    public void deleteAll(String userId) {
        String key = PREFIX + userId;
        redisTemplate.delete(key);
    }
}
