package com.example.Petbulance_BE.domain.banner.repository;

import com.example.Petbulance_BE.domain.banner.dto.response.BannerDetailResDto;
import com.example.Petbulance_BE.domain.banner.dto.response.HomeBannerListResDto;
import com.example.Petbulance_BE.domain.banner.dto.response.PagingAdminBannerListResDto;

import java.util.List;

public interface BannerRepositoryCustom {
    PagingAdminBannerListResDto adminBannerList(int page, int size);

    BannerDetailResDto bannerDetail(Long bannerId);

    List<HomeBannerListResDto> homeBannerList();

}
