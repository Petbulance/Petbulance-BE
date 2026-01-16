package com.example.Petbulance_BE.domain.banner.repository;

import com.example.Petbulance_BE.domain.banner.dto.response.BannerDetailResDto;
import com.example.Petbulance_BE.domain.banner.dto.response.PagingAdminBannerListResDto;
import com.example.Petbulance_BE.domain.banner.entity.Banner;

public interface BannerRepositoryCustom {
    PagingAdminBannerListResDto adminBannerList(int page, int size);

    BannerDetailResDto bannerDetail(Banner banner);
}
