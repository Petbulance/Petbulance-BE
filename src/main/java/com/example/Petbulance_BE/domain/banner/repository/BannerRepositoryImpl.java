package com.example.Petbulance_BE.domain.banner.repository;


import com.example.Petbulance_BE.domain.banner.dto.response.BannerListResDto;
import com.example.Petbulance_BE.domain.banner.dto.response.PagingAdminBannerListResDto;
import com.example.Petbulance_BE.domain.banner.entity.QBanner;
import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;

import java.util.List;

@RequiredArgsConstructor
public class BannerRepositoryImpl implements BannerRepositoryCustom{
    private final JPAQueryFactory queryFactory;

    @Override
    public PagingAdminBannerListResDto adminBannerList(int page, int size) {

        QBanner banner = QBanner.banner;

        // page는 1부터 시작
        long offset = (long) (page - 1) * size;

        // 1️⃣ 페이징 대상 ID 먼저 조회
        List<Long> ids = queryFactory
                .select(banner.id)
                .from(banner)
                .orderBy(banner.createdAt.desc(), banner.id.desc())
                .offset(offset)
                .limit(size)
                .fetch();

        // 조회 결과 없음
        if (ids.isEmpty()) {
            return new PagingAdminBannerListResDto(
                    List.of(),
                    page,
                    size,
                    0,
                    0,
                    false,
                    page > 1
            );
        }

        // 2️⃣ 실제 목록 조회
        List<BannerListResDto> content = queryFactory
                .select(
                        Projections.constructor(
                                BannerListResDto.class,
                                banner.id,
                                banner.noticeStatus,
                                banner.title,
                                banner.postStatus
                        )
                )
                .from(banner)
                .where(banner.id.in(ids))
                .orderBy(banner.createdAt.desc(), banner.id.desc())
                .fetch();

        // 3️⃣ 전체 개수
        long totalElements = queryFactory
                .select(banner.id.count())
                .from(banner)
                .fetchOne();

        int totalPages = (int) Math.ceil((double) totalElements / size);

        boolean hasNext = page < totalPages;
        boolean hasPrev = page > 1;

        return new PagingAdminBannerListResDto(
                content,
                page,
                size,
                totalPages,
                totalElements,
                hasNext,
                hasPrev
        );
    }

}
