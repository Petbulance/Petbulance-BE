package com.example.Petbulance_BE.domain.adminlog.repository;

import com.example.Petbulance_BE.domain.adminlog.dto.response.AdminActionLogListResDto;
import com.example.Petbulance_BE.domain.adminlog.dto.response.PagingAdminActionLogListResDto;
import com.example.Petbulance_BE.domain.adminlog.entity.QAdminActionLog;
import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;

import java.util.List;

@RequiredArgsConstructor
public class AdminActionLogRepositoryImpl implements AdminActionLogRepositoryCustom{
    private final JPAQueryFactory queryFactory;

    @Override
    public PagingAdminActionLogListResDto adminActionLogList(int page, int size) {
        QAdminActionLog log = QAdminActionLog.adminActionLog;

        long offset = (long) (page - 1) * size;


        List<Long> ids = queryFactory
                .select(log.id)
                .from(log)
                .orderBy(log.createdAt.desc(), log.id.desc())
                .offset(offset)
                .limit(size)
                .fetch();

        if (ids.isEmpty()) {
            return new PagingAdminActionLogListResDto(
                    List.of(),
                    page,
                    size,
                    0,
                    0,
                    false,
                    page > 1
            );
        }

        List<AdminActionLogListResDto> content = queryFactory
                .select(
                        Projections.constructor(
                                AdminActionLogListResDto.class,
                                log.id,
                                log.admin.nickname,
                                log.pageType,
                                log.description,
                                log.resultType,
                                log.createdAt
                        )
                )
                .from(log)
                .leftJoin(log.admin)
                .where(log.id.in(ids))
                .orderBy(log.createdAt.desc(), log.id.desc())
                .fetch();

        Long totalElements = queryFactory
                .select(log.id.count())
                .from(log)
                .fetchOne();

        long total = totalElements == null ? 0 : totalElements;

        int totalPages = (int) Math.ceil((double) total / size);

        boolean hasNext = page < totalPages;
        boolean hasPrev = page > 1;

        return new PagingAdminActionLogListResDto(
                content,
                page,
                size,
                totalPages,
                total,
                hasNext,
                hasPrev
        );
    }
}
