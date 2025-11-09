package com.example.Petbulance_BE.domain.recent.repository;

import com.example.Petbulance_BE.domain.recent.dto.response.RecentCommunityResDto;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.RedisTemplate;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
class RecentCommunitySearchRepositoryTest {
    @Autowired
    private RecentCommunitySearchRepository recentCommunitySearchRepository;

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    private final String userId = "user:100";

    @AfterEach
    void tearDown() {
        redisTemplate.delete("recent_keywords:" + userId);
    }

    @Test
    void 키워드를_저장하고_조회할_수_있다() {
        // given
        recentCommunitySearchRepository.saveKeyword(userId, "햄스터");
        recentCommunitySearchRepository.saveKeyword(userId, "도마뱀");

        // when
        List<RecentCommunityResDto> result = recentCommunitySearchRepository.getRecentKeywords(userId);

        // then
        assertThat(result).hasSize(2);
        assertThat(result.get(0).getKeyword()).isEqualTo("도마뱀"); // 최신순 (leftPush)
        assertThat(result.get(1).getKeyword()).isEqualTo("햄스터");
    }

    @Test
    void 키워드를_삭제할_수_있다() {
        // given
        recentCommunitySearchRepository.saveKeyword(userId, "햄스터");
        recentCommunitySearchRepository.saveKeyword(userId, "도마뱀");
        List<RecentCommunityResDto> beforeDelete = recentCommunitySearchRepository.getRecentKeywords(userId);
        String deleteId = beforeDelete.get(0).getKeywordId(); // "도마뱀"의 ID

        // when
        recentCommunitySearchRepository.deleteKeyword(userId, deleteId);

        // then
        List<RecentCommunityResDto> afterDelete = recentCommunitySearchRepository.getRecentKeywords(userId);
        assertThat(afterDelete).hasSize(1);
        assertThat(afterDelete.get(0).getKeyword()).isEqualTo("햄스터");
    }

    @Test
    void 최대_5개까지만_저장된다() {
        // given
        for (int i = 1; i <= 7; i++) {
            recentCommunitySearchRepository.saveKeyword(userId, "검색어" + i);
        }

        // when
        List<RecentCommunityResDto> result = recentCommunitySearchRepository.getRecentKeywords(userId);

        // then
        assertThat(result).hasSize(5);
        assertThat(result.get(0).getKeyword()).isEqualTo("검색어7"); // 최신
        assertThat(result.get(4).getKeyword()).isEqualTo("검색어3"); // 오래된 순
    }
}