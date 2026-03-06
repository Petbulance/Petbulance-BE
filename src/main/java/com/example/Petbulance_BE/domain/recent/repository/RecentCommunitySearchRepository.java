package com.example.Petbulance_BE.domain.recent.repository;

import com.example.Petbulance_BE.domain.recent.dto.response.RecentCommunityResDto;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Repository
@RequiredArgsConstructor
public class RecentCommunitySearchRepository {
    private final RedisTemplate<String, Object> redisTemplate;
    private static final int MAX_KEYWORDS = 5;

    public void saveKeyword(String userId, String keyword) {
        String key = "recent_keywords:" + userId;

        List<Object> objects = redisTemplate.opsForList().range(key, 0, -1);

        if (objects != null) {
            for (Object o : objects) {
                RecentCommunityResDto dto = (RecentCommunityResDto) o;

                // 같은 keyword가 존재하면 삭제
                if (dto.getKeyword().equals(keyword)) {
                    redisTemplate.opsForList().remove(key, 1, dto);
                    break;
                }
            }
        }

        // 새로 생성 (createdAt 갱신)
        RecentCommunityResDto keywordDto = RecentCommunityResDto.builder()
                .keywordId(UUID.randomUUID().toString()) // 기존 id 유지하고 싶으면 따로 처리 가능
                .keyword(keyword)
                .createdAt(LocalDateTime.now())
                .build();

        redisTemplate.opsForList().leftPush(key, keywordDto);
        redisTemplate.opsForList().trim(key, 0, MAX_KEYWORDS - 1);
        redisTemplate.expire(key, Duration.ofDays(30));
    }

    public List<RecentCommunityResDto> getRecentKeywords(String userId) {
        String key = "recent_keywords:" + userId;
        List<Object> objects = redisTemplate.opsForList().range(key, 0, -1);
        if (objects == null) return List.of();
        return objects.stream()
                .map(o -> (RecentCommunityResDto) o)
                .collect(Collectors.toList());
    }

    public void deleteKeyword(String userId, String keywordId) {
        String key = "recent_keywords:" + userId;
        List<Object> objects = redisTemplate.opsForList().range(key, 0, -1);
        if (objects == null || objects.isEmpty()) return;

        for (Object o : objects) {
            RecentCommunityResDto dto = (RecentCommunityResDto) o;
            if (dto.getKeywordId().equals(keywordId)) {
                redisTemplate.opsForList().remove(key, 1, dto);
                break;
            }
        }
    }

    public void deleteAllKeywords(String userId) {
        String key = "recent_keywords:" + userId;
        redisTemplate.delete(key);
    }

}
