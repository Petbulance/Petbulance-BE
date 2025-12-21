package com.example.Petbulance_BE.domain.banner.controller;

import com.example.Petbulance_BE.domain.banner.dto.request.CreateBannerReqDto;
import com.example.Petbulance_BE.domain.banner.dto.response.CreateBannerResDto;
import com.example.Petbulance_BE.domain.banner.dto.response.PagingAdminBannerListResDto;
import com.example.Petbulance_BE.domain.banner.service.BannerService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/admin/banner")
@RequiredArgsConstructor
public class AdminBannerController {
    private BannerService bannerService;

    @GetMapping
    public PagingAdminBannerListResDto adminBannerList(@RequestParam(required = false) Long lastBannerId,
                                                       @RequestParam(defaultValue = "10") int pageSize) {
        Pageable pageable = PageRequest.of(0, pageSize);
        return bannerService.adminNoticeList(lastBannerId, pageable);
    }

    @PostMapping
    public CreateBannerResDto createBanner(@RequestBody @Valid CreateBannerReqDto reqDto) {
        return bannerService.createBanner(reqDto);
    }
}
