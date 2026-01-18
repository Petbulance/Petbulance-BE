package com.example.Petbulance_BE.domain.banner.repository;

import com.example.Petbulance_BE.domain.banner.dto.response.HomeBannerListResDto;

import java.util.List;

public interface BannerRepositoryCustom {
    List<HomeBannerListResDto> homeBannerList();

}
