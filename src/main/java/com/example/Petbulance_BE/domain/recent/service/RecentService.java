package com.example.Petbulance_BE.domain.recent.service;

import com.example.Petbulance_BE.domain.recent.dto.response.RecentCommunityResDto;
import com.example.Petbulance_BE.domain.recent.repository.RecentCommunitySearchRepository;
import com.example.Petbulance_BE.domain.user.entity.Users;
import com.example.Petbulance_BE.global.util.UserUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;

@Service
@Transactional
@RequiredArgsConstructor
public class RecentService {
    private final RecentCommunitySearchRepository recentCommunitySearchRepository;

    public void saveRecentCommunitySearch(String keyword, Users currentUser) {
       recentCommunitySearchRepository.saveKeyword(currentUser.getId(), keyword);
    }

    public List<RecentCommunityResDto> recentCommunityList() {
        return recentCommunitySearchRepository.getRecentKeywords(Objects.requireNonNull(UserUtil.getCurrentUser()).getId());
    }

    public void deleteRecentCommunitySearch(String keywordId) {
        recentCommunitySearchRepository.deleteKeyword(Objects.requireNonNull(UserUtil.getCurrentUser()).getId(), keywordId);
    }

}
