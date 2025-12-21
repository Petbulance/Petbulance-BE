package com.example.Petbulance_BE.domain.banner.service;

import com.example.Petbulance_BE.domain.banner.dto.request.CreateBannerReqDto;
import com.example.Petbulance_BE.domain.banner.dto.response.CreateBannerResDto;
import com.example.Petbulance_BE.domain.banner.dto.response.PagingAdminBannerListResDto;
import jakarta.validation.Valid;
import org.springframework.data.domain.Pageable;

public class BannerService {
    public CreateBannerResDto createBanner(@Valid CreateBannerReqDto reqDto) {
        return null;
    }

    public PagingAdminBannerListResDto adminNoticeList(Long lastBannerId, Pageable pageable) {

        return null;
    }
}
