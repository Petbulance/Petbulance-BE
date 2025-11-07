package com.example.Petbulance_BE.domain.recent.controller;

import com.example.Petbulance_BE.domain.recent.dto.response.DeleteKeywordResDto;
import com.example.Petbulance_BE.domain.recent.dto.response.RecentCommunityResDto;
import com.example.Petbulance_BE.domain.recent.service.RecentService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequestMapping("/recents")
@RestController
@RequiredArgsConstructor
public class RecentController {
    private final RecentService recentService;

    @GetMapping("/community")
    public List<RecentCommunityResDto> recentCommunityList() {
        return recentService.recentCommunityList();
    }

    @DeleteMapping("/community/{keywordId}")
    public DeleteKeywordResDto deleteRecentCommunitySearch(@PathVariable("keywordId") String keywordId) {
        recentService.deleteRecentCommunitySearch(keywordId);
        return new DeleteKeywordResDto(true, "최근 검색어가 성공적으로 삭제되었습니다.");
    }
}
