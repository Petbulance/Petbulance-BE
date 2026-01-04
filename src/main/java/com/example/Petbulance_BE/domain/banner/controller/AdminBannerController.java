package com.example.Petbulance_BE.domain.banner.controller;

import com.example.Petbulance_BE.domain.banner.dto.request.CreateBannerReqDto;
import com.example.Petbulance_BE.domain.banner.dto.request.UpdateBannerReqDto;
import com.example.Petbulance_BE.domain.banner.dto.response.BannerResDto;
import com.example.Petbulance_BE.domain.banner.dto.response.PagingAdminBannerListResDto;
import com.example.Petbulance_BE.domain.banner.service.BannerService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/admin/banner")
@RequiredArgsConstructor
public class AdminBannerController {
    private BannerService bannerService;

    @GetMapping
    public PagingAdminBannerListResDto adminBannerList(@RequestParam(defaultValue = "1") int page,
                                                       @RequestParam(defaultValue = "20") int size) {
        return bannerService.adminNoticeList(page, size);
    }

    @PostMapping
    public BannerResDto createBanner(@RequestBody @Valid CreateBannerReqDto reqDto) {
        return bannerService.createBanner(reqDto);
    }

    @PutMapping
    public BannerResDto updateBanner(@RequestBody @Valid UpdateBannerReqDto reqDto) {
        return bannerService.updateBanner(reqDto);
    }
}
