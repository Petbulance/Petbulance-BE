package com.example.Petbulance_BE.domain.adminlog.repository;

import com.example.Petbulance_BE.domain.adminlog.dto.response.AdminActionLogListResDto;
import com.example.Petbulance_BE.domain.adminlog.dto.response.PagingAdminActionLogListResDto;
import com.example.Petbulance_BE.domain.adminlog.entity.QAdminActionLog;
import com.example.Petbulance_BE.domain.adminlog.type.AdminActionResult;
import com.example.Petbulance_BE.domain.adminlog.type.AdminPageType;
import com.querydsl.core.types.Predicate;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;

import java.util.List;

@RequiredArgsConstructor
public class AdminActionLogRepositoryImpl implements AdminActionLogRepositoryCustom {
    private final JPAQueryFactory queryFactory;
    QAdminActionLog log = QAdminActionLog.adminActionLog;

    @Override
    public PagingAdminActionLogListResDto adminActionLogList(String name, AdminPageType pageType, AdminActionResult resultType, int page, int size) {

        long offset = (long) (page - 1) * size;


        List<Long> ids = queryFactory
                .select(log.id)
                .from(log)
                .leftJoin(log.admin)
                .where(
                        containsNickname(name),
                        eqPageType(pageType),
                        eqResultType(resultType)
                )
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
                .select(log.count())
                .from(log)
                .leftJoin(log.admin)
                .where(
                        containsNickname(name),
                        eqPageType(pageType),
                        eqResultType(resultType)
                )
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

    private BooleanExpression containsNickname(String name) {
        if (name == null || name.trim().isEmpty() || name.equals("null")) {
            return null;
        }
        return log.admin.nickname.contains(name);
    }

    private BooleanExpression eqPageType(AdminPageType pageType) {
        return pageType == null ? null : log.pageType.eq(pageType);
    }

    private BooleanExpression eqResultType(AdminActionResult resultType) {
        return resultType == null ? null : log.resultType.eq(resultType);
    }
}
