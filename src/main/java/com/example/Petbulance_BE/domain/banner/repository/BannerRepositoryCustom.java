package com.example.Petbulance_BE.domain.banner.repository;

import com.example.Petbulance_BE.domain.banner.dto.response.PagingAdminBannerListResDto;

public interface BannerRepositoryCustom {
    PagingAdminBannerListResDto adminBannerList(int page, int size);
}
