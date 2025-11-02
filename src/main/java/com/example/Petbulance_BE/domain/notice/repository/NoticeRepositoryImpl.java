package com.example.Petbulance_BE.domain.notice.repository;

import com.example.Petbulance_BE.domain.notice.dto.response.NoticeListResDto;
import com.example.Petbulance_BE.domain.notice.dto.response.PagingNoticeListResDto;
import com.example.Petbulance_BE.domain.notice.entity.QNotice;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class NoticeRepositoryImpl implements NoticeRepositoryCustom{
    private final JPAQueryFactory queryFactory;

    @Override
    public PagingNoticeListResDto findNoticeList(Long lastNoticeId, Pageable pageable) {
        QNotice n = QNotice.notice;

        BooleanBuilder whereBuilder = new BooleanBuilder();
        if (lastNoticeId != null) {
            whereBuilder.and(n.id.lt(lastNoticeId));
        }

        List<NoticeListResDto> results = queryFactory
                .select(Projections.constructor(
                        NoticeListResDto.class,
                        n.id,
                        n.isImportant,
                        n.title,
                        n.content,
                        n.createdAt
                ))
                .from(n)
                .where(whereBuilder)
                .orderBy(n.isImportant.desc(), n.createdAt.desc())
                .limit(pageable.getPageSize() + 1)
                .fetch();

        boolean hasNext = results.size() > pageable.getPageSize();
        if (hasNext) {
            results.remove(results.size() - 1);
        }

        return new PagingNoticeListResDto(results, hasNext);
    }

}
