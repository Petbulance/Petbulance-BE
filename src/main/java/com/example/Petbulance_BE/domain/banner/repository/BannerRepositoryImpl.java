package com.example.Petbulance_BE.domain.banner.repository;


import com.example.Petbulance_BE.domain.banner.dto.response.BannerDetailResDto;
import com.example.Petbulance_BE.domain.banner.dto.response.BannerListResDto;
import com.example.Petbulance_BE.domain.banner.dto.response.HomeBannerListResDto;
import com.example.Petbulance_BE.domain.banner.dto.response.PagingAdminBannerListResDto;
import com.example.Petbulance_BE.domain.banner.entity.Banner;
import com.example.Petbulance_BE.domain.banner.entity.QBanner;
import com.example.Petbulance_BE.domain.notice.entity.QNotice;
import com.example.Petbulance_BE.domain.notice.type.PostStatus;
import com.example.Petbulance_BE.domain.user.entity.QUsers;
import com.example.Petbulance_BE.global.common.error.exception.CustomException;
import com.example.Petbulance_BE.global.common.error.exception.ErrorCode;
import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;

import java.util.List;

@RequiredArgsConstructor
public class BannerRepositoryImpl implements BannerRepositoryCustom{
    private final JPAQueryFactory queryFactory;
    QBanner banner = QBanner.banner;

    @Override
    public PagingAdminBannerListResDto adminBannerList(int page, int size) {
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

    @Override
    public BannerDetailResDto bannerDetail(Long bannerId) {

        QBanner banner = QBanner.banner;
        QNotice notice = QNotice.notice;
        QUsers users = QUsers.users;

        BannerDetailResDto result = queryFactory
                .select(Projections.constructor(BannerDetailResDto.class,
                        banner.id,
                        notice.title,
                        users.nickname,
                        banner.postStatus,
                        banner.noticeStatus,
                        banner.title,
                        banner.startDate,
                        banner.endDate,
                        banner.fileUrl
                ))
                .from(banner)
                .leftJoin(banner.notice, notice)
                .leftJoin(banner.users, users)
                .where(banner.id.eq(bannerId))
                .fetchOne();

        if (result == null) {
            throw new CustomException(ErrorCode.NOT_FOUND_BANNER);
        }

        return result;
    }


    @Override
    public List<HomeBannerListResDto> homeBannerList() {
        return queryFactory
                .select(Projections.constructor(HomeBannerListResDto.class,
                        banner.id,
                        banner.startDate,
                        banner.endDate,
                        banner.postStatus,
                        banner.noticeStatus,
                        banner.notice.id,
                        banner.title,
                        banner.fileUrl
                        ))
                .from(banner)
                .where(banner.postStatus.eq(PostStatus.ACTIVE))
                .orderBy(banner.createdAt.desc())
                .limit(5)
                .fetch();
    }

}
