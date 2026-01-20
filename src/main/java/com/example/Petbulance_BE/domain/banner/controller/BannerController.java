package com.example.Petbulance_BE.domain.banner.controller;

import com.example.Petbulance_BE.domain.banner.dto.response.HomeBannerListResDto;
import com.example.Petbulance_BE.domain.banner.service.BannerService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/banners")
@RequiredArgsConstructor
public class BannerController {
    private final BannerService bannerService;
    @GetMapping("/home")
    public List<HomeBannerListResDto> homeBannerList() {
        return bannerService.homeBannerList();
    }
}
