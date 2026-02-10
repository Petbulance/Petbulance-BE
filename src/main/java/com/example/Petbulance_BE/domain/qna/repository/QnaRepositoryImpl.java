package com.example.Petbulance_BE.domain.qna.repository;

import com.example.Petbulance_BE.domain.notice.dto.response.PagingNoticeListResDto;
import com.example.Petbulance_BE.domain.qna.dto.response.AdminQnaListResDto;
import com.example.Petbulance_BE.domain.qna.dto.response.PagingAdminQnaListResDto;
import com.example.Petbulance_BE.domain.qna.dto.response.PagingQnaListResDto;
import com.example.Petbulance_BE.domain.qna.dto.response.QnaListResDto;
import com.example.Petbulance_BE.domain.qna.entity.QQna;
import com.example.Petbulance_BE.domain.user.entity.QUsers;
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
    public PagingAdminQnaListResDto adminQnaList(int page, int size) {
        QQna qna = QQna.qna;
        QUsers user = QUsers.users;

        long offset = (long) (page - 1) * size;

        // 1) id 목록 가져오기
        List<Long> ids = queryFactory
                .select(qna.id)
                .from(qna)
                .orderBy(qna.createdAt.desc(), qna.id.desc())
                .offset(offset)
                .limit(size)
                .fetch();

        if (ids.isEmpty()) {
            return new PagingAdminQnaListResDto(
                    List.of(), page, size, 0, 0, false, page > 1
            );
        }

        //  2) 실제 데이터 조회
        List<AdminQnaListResDto> content = queryFactory
                .select(
                        Projections.constructor(
                                AdminQnaListResDto.class,
                                qna.id,
                                qna.status,
                                qna.title,
                                qna.content,
                                user.nickname,
                                qna.createdAt
                        )
                )
                .from(qna)
                .join(user).on(qna.user.eq(user))
                .where(qna.id.in(ids))
                .orderBy(qna.createdAt.desc(), qna.id.desc())
                .fetch();

        // 🔹 3) 전체 개수
        long totalElements = queryFactory
                .select(qna.id.count())
                .from(qna)
                .fetchOne();

        int totalPages = (int) Math.ceil((double) totalElements / size);

        boolean hasNext = page < totalPages;
        boolean hasPrev = page > 1;

        return new PagingAdminQnaListResDto(
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
