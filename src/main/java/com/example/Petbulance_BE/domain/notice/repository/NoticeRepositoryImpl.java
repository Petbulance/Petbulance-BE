package com.example.Petbulance_BE.domain.notice.repository;

import com.example.Petbulance_BE.domain.notice.dto.response.AdminNoticeListResDto;
import com.example.Petbulance_BE.domain.notice.dto.response.NoticeListResDto;
import com.example.Petbulance_BE.domain.notice.dto.response.PagingAdminNoticeListResDto;
import com.example.Petbulance_BE.domain.notice.dto.response.PagingNoticeListResDto;
import com.example.Petbulance_BE.domain.notice.entity.Notice;
import com.example.Petbulance_BE.domain.notice.entity.QNotice;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
@RequiredArgsConstructor
public class NoticeRepositoryImpl implements NoticeRepositoryCustom{
    private final JPAQueryFactory queryFactory;
    QNotice n = QNotice.notice;

    @Override
    public PagingNoticeListResDto findNoticeList(
            Long lastNoticeId,
            LocalDateTime lastCreatedAt,
            Pageable pageable
    ) {
        BooleanBuilder whereBuilder = new BooleanBuilder();

        // 커서 조건 (createdAt → id)
        if (lastNoticeId != null && lastCreatedAt != null) {
            whereBuilder.and(
                    n.createdAt.lt(lastCreatedAt)
                            .or(
                                    n.createdAt.eq(lastCreatedAt)
                                            .and(n.id.lt(lastNoticeId))
                            )
            );
        }

        List<NoticeListResDto> results = queryFactory
                .select(Projections.constructor(
                        NoticeListResDto.class,
                        n.id,
                        n.noticeStatus,
                        n.title,
                        n.content,
                        n.createdAt
                ))
                .from(n)
                .where(whereBuilder)
                .orderBy(
                        n.createdAt.desc(),
                        n.id.desc()
                )
                .limit(pageable.getPageSize() + 1)
                .fetch();

        boolean hasNext = results.size() > pageable.getPageSize();
        if (hasNext) {
            results.remove(results.size() - 1);
        }

        return new PagingNoticeListResDto(results, hasNext);
    }

    @Override
    public Notice findPreviousNotice(Long noticeId) {

        Notice current = queryFactory
                .selectFrom(n)
                .where(n.id.eq(noticeId))
                .fetchOne();

        if (current == null) return null;

        return queryFactory
                .selectFrom(n)
                .where(
                        n.createdAt.lt(current.getCreatedAt())
                                .or(
                                        n.createdAt.eq(current.getCreatedAt())
                                                .and(n.id.lt(noticeId))
                                )
                )
                .orderBy(n.createdAt.desc(), n.id.desc())
                .fetchFirst();
    }

    @Override
    public Notice findNextNotice(Long noticeId) {

        Notice current = queryFactory
                .selectFrom(n)
                .where(n.id.eq(noticeId))
                .fetchOne();

        if (current == null) return null;

        return queryFactory
                .selectFrom(n)
                .where(
                        n.createdAt.gt(current.getCreatedAt())
                                .or(
                                        n.createdAt.eq(current.getCreatedAt())
                                                .and(n.id.gt(noticeId))
                                )
                )
                .orderBy(n.createdAt.asc(), n.id.asc())
                .fetchFirst();
    }

    @Override
    public PagingAdminNoticeListResDto adminNoticeList(Long lastNoticeId, Pageable pageable) {

        QNotice notice = QNotice.notice;

        int pageSize = pageable.getPageSize();
        int limit = pageSize + 1; // hasNext 판별용

        List<AdminNoticeListResDto> rows = queryFactory
                .select(Projections.constructor(
                        AdminNoticeListResDto.class,
                        notice.id,
                        notice.title,
                        notice.noticeStatus,
                        notice.createdAt
                ))
                .from(notice)
                .where(ltNoticeId(lastNoticeId, notice))
                .orderBy(notice.id.desc())   // 최신순
                .limit(limit)
                .fetch();

        boolean hasNext = false;
        if (rows.size() > pageSize) {
            hasNext = true;
            rows = rows.subList(0, pageSize);
        }

        return new PagingAdminNoticeListResDto(rows, hasNext);
    }

    private BooleanExpression ltNoticeId(Long lastNoticeId, QNotice notice) {
        return lastNoticeId == null ? null : notice.id.lt(lastNoticeId);
    }

}
