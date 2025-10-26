package com.example.Petbulance_BE.domain.post.repository;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Repository;

import java.time.Duration;

@Repository
@RequiredArgsConstructor
public class PostViewCountRepository {

    private final StringRedisTemplate redisTemplate;

    // Redis Key Format
    // 조회수 카운트: view::post::{postId}::count
    // 사용자별 조회 기록: view::post::{postId}::users
    private static final String VIEW_COUNT_KEY_FORMAT = "view::post::%s::count";
    private static final String VIEW_USERS_KEY_FORMAT = "view::post::%s::users";

    public Long read(Long postId) {
        String result = redisTemplate.opsForValue().get(generateCountKey(postId));
        return result == null ? 0L : Long.parseLong(result);
    }

    // 아직 조회하지 않은 경우에만 조회수 증가
    public Long increaseIfNotViewed(Long postId, String userId) {
        String usersKey = generateUsersKey(postId);
        String countKey = generateCountKey(postId);

        // 사용자 집합(Set)에 추가 → 새로 추가되었을 때만 true 반환
        Boolean isNewViewer = redisTemplate.opsForSet().add(usersKey, userId) == 1;

        if (Boolean.TRUE.equals(isNewViewer)) {
            return redisTemplate.opsForValue().increment(countKey);
        }
        // 이미 조회한 사용자면 현재 조회수 그대로 반환
        String result = redisTemplate.opsForValue().get(countKey);
        return result == null ? 0L : Long.parseLong(result);
    }

    private String generateCountKey(Long postId) {
        return VIEW_COUNT_KEY_FORMAT.formatted(postId);
    }

    private String generateUsersKey(Long postId) {
        return VIEW_USERS_KEY_FORMAT.formatted(postId);
    }
}
