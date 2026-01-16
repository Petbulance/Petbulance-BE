package com.example.Petbulance_BE.domain.banner.repository;

import com.example.Petbulance_BE.domain.banner.dto.response.BannerDetailResDto;
import com.example.Petbulance_BE.domain.banner.dto.response.HomeBannerListResDto;
import com.example.Petbulance_BE.domain.banner.dto.response.PagingAdminBannerListResDto;
import com.example.Petbulance_BE.domain.banner.entity.Banner;

import java.util.List;

public interface BannerRepositoryCustom {
    PagingAdminBannerListResDto adminBannerList(int page, int size);

    BannerDetailResDto bannerDetail(Banner banner);

    List<HomeBannerListResDto> homeBannerList();

}
