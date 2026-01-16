package com.example.Petbulance_BE.domain.banner.controller;

import com.example.Petbulance_BE.domain.banner.dto.request.BannerImageCheckReqDto;
import com.example.Petbulance_BE.domain.banner.dto.request.CreateBannerReqDto;
import com.example.Petbulance_BE.domain.banner.dto.request.UpdateBannerReqDto;
import com.example.Petbulance_BE.domain.banner.dto.response.BannerDetailResDto;
import com.example.Petbulance_BE.domain.banner.dto.response.BannerImageCheckResDto;
import com.example.Petbulance_BE.domain.banner.dto.response.BannerResDto;
import com.example.Petbulance_BE.domain.banner.dto.response.PagingAdminBannerListResDto;
import com.example.Petbulance_BE.domain.banner.service.BannerService;
import com.example.Petbulance_BE.domain.notice.dto.response.NoticeImageCheckResDto;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/admin/banners")
@RequiredArgsConstructor
public class AdminBannerController {
    private BannerService bannerService;

    @GetMapping
    public PagingAdminBannerListResDto adminBannerList(@RequestParam(defaultValue = "1") int page,
                                                       @RequestParam(defaultValue = "20") int size) {
        return bannerService.adminBannerList(page, size);
    }

    @PostMapping
    public BannerResDto createBanner(@RequestBody @Valid CreateBannerReqDto reqDto) {
        return bannerService.createBanner(reqDto);
    }

    @GetMapping("/save/sucess")
    public BannerImageCheckResDto bannerFileSaveCheckProcess(@RequestBody BannerImageCheckReqDto reqDto) {
        bannerService.bannerFileSaveCheckProcess(reqDto);
        return new BannerImageCheckResDto("성공적으로 이미지가 등록되었습니다.");
    }

    @PutMapping("/{bannerId}")
    public BannerResDto updateBanner(@PathVariable(name = "bannerId") Long bannerId, @RequestBody @Valid UpdateBannerReqDto reqDto) {
        return bannerService.updateBanner(bannerId, reqDto);
    }

    @GetMapping("/{bannerId}")
    public BannerDetailResDto bannerDetail(@PathVariable(name = "bannerId") Long bannerId) {
        return bannerService.bannerDetail(bannerId);
    }

}
