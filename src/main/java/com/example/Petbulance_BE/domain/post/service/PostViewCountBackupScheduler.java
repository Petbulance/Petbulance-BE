package com.example.Petbulance_BE.domain.post.service;

import com.example.Petbulance_BE.domain.post.repository.PostViewCountBackUpRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.data.redis.core.RedisTemplate;

import java.util.Set;

@Slf4j
@Component
@RequiredArgsConstructor
public class PostViewCountBackupScheduler {

    private final RedisTemplate<String, Object> redisTemplate;
    private final PostViewCountBackUpRepository viewCountRepository;

    private static final String REDIS_KEY_PREFIX = "post:viewcount:";

    @Scheduled(cron = "0 0 * * * *")
    @Transactional
    public void backupViewCounts() {
        Set<String> keys = redisTemplate.keys(REDIS_KEY_PREFIX + "*");

        if (keys == null || keys.isEmpty()) return;

        for (String key : keys) {
            try {
                Long postId = Long.parseLong(key.replace(REDIS_KEY_PREFIX, ""));
                Object value = redisTemplate.opsForValue().get(key);

                if (value != null) {
                    long count = ((Number) value).longValue();
                    viewCountRepository.upsertViewCount(postId, count);
                    redisTemplate.delete(key); // Redis에서 제거 (누적된 것만 MySQL에 반영)
                }
            } catch (Exception e) {
                log.warn("ViewCount 백업 실패 key={}", key, e);
            }
        }
    }
}
