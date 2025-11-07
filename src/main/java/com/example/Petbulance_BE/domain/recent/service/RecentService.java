package com.example.Petbulance_BE.domain.recent.service;

import com.example.Petbulance_BE.domain.recent.repository.RecentCommunitySearchRepository;
import com.example.Petbulance_BE.domain.user.entity.Users;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class RecentService {
    private final RecentCommunitySearchRepository recentCommunitySearchRepository;

    public void saveRecentCommunitySearch(String keyword, Users currentUser) {
       recentCommunitySearchRepository.saveKeyword(currentUser.getId(), keyword);
    }
}
