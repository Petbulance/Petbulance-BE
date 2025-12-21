package com.example.Petbulance_BE.domain.qna.repository;

import com.example.Petbulance_BE.domain.notice.dto.response.PagingNoticeListResDto;
import com.example.Petbulance_BE.domain.qna.dto.response.AdminQnaListResDto;
import com.example.Petbulance_BE.domain.qna.dto.response.PagingAdminQnaListResDto;
import com.example.Petbulance_BE.domain.qna.dto.response.PagingQnaListResDto;
import com.example.Petbulance_BE.domain.qna.dto.response.QnaListResDto;
import com.example.Petbulance_BE.domain.qna.entity.QQna;
import com.example.Petbulance_BE.domain.user.entity.Users;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class QnaRepositoryImpl implements QnaRepositoryCustom{
    private final JPAQueryFactory queryFactory;
    QQna q = QQna.qna;

    @Override
    public PagingQnaListResDto findQnaList(Users currentUser, Long lastQnaId, Pageable pageable) {
        BooleanBuilder whereBuilder = new BooleanBuilder();
        if(lastQnaId != null) {
            whereBuilder.and(q.id.lt(lastQnaId));
        }
        whereBuilder.and(q.user.eq(currentUser));

        List<QnaListResDto> results = queryFactory
                .select(Projections.constructor(
                        QnaListResDto.class,
                        q.id,
                        q.title,
                        q.content,
                        q.createdAt,
                        q.status
                ))
                .from(q)
                .where(whereBuilder)
                .orderBy(q.createdAt.desc(), q.id.desc())
                .limit(pageable.getPageSize() + 1)
                .fetch();

        boolean hasNext = results.size() > pageable.getPageSize();
        if (hasNext) {
            results.remove(results.size() - 1);
        }

        return new PagingQnaListResDto(results, hasNext);
    }

    @Override
    public PagingAdminQnaListResDto adminQnaList(Long lastQnaId, Pageable pageable, String keyword) {

        QQna qna = QQna.qna;

        int limit = pageable.getPageSize() + 1;

        List<AdminQnaListResDto> rows = queryFactory
                .select(Projections.constructor(
                        AdminQnaListResDto.class,
                        qna.id,
                        qna.status,
                        qna.title,
                        qna.user.nickname,
                        qna.createdAt
                ))
                .from(qna)
                .where(
                        ltQnaId(lastQnaId, qna),
                        containsKeyword(keyword, qna)
                )
                .orderBy(qna.id.desc())
                .limit(limit)
                .fetch();

        boolean hasNext = false;
        if (rows.size() > pageable.getPageSize()) {
            hasNext = true;
            rows = rows.subList(0, pageable.getPageSize());
        }

        return new PagingAdminQnaListResDto(rows, hasNext);
    }

    private BooleanExpression ltQnaId(Long lastQnaId, QQna qna) {
        return lastQnaId == null ? null : qna.id.lt(lastQnaId);
    }

    private BooleanExpression containsKeyword(String keyword, QQna qna) {
        if (keyword == null || keyword.isBlank()) {
            return null;
        }
        return qna.title.contains(keyword);
        // 필요하면 ↓ 확장 가능
        // .or(qna.content.contains(keyword))
    }

}
