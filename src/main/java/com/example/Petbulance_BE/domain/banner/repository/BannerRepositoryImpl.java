package com.example.Petbulance_BE.domain.banner.repository;


import com.example.Petbulance_BE.domain.banner.dto.response.HomeBannerListResDto;
import com.example.Petbulance_BE.domain.banner.entity.QBanner;
import com.example.Petbulance_BE.domain.notice.entity.QNotice;
import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;

import java.time.LocalDate;
import java.util.List;

@RequiredArgsConstructor
public class BannerRepositoryImpl implements BannerRepositoryCustom{
    private final JPAQueryFactory queryFactory;
    QBanner banner = QBanner.banner;
    QNotice notice = QNotice.notice;

    @Override
    public List<HomeBannerListResDto> homeBannerList() {
        LocalDate now = LocalDate.now();

        return queryFactory
                .select(Projections.constructor(HomeBannerListResDto.class,
                        banner.id,
                        banner.startDate,
                        banner.endDate,
                        notice.id,
                        banner.fileUrl
                ))
                .from(notice)
                .join(notice.banner, banner)
                .where(
                        banner.startDate.loe(now),
                        banner.endDate.goe(now)
                )
                .orderBy(banner.createdAt.desc())
                .limit(5)
                .fetch();
    }

}
