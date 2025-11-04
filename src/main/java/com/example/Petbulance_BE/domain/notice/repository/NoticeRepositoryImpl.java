package com.example.Petbulance_BE.domain.notice.repository;

import com.example.Petbulance_BE.domain.notice.dto.response.NoticeListResDto;
import com.example.Petbulance_BE.domain.notice.dto.response.PagingNoticeListResDto;
import com.example.Petbulance_BE.domain.notice.entity.Notice;
import com.example.Petbulance_BE.domain.notice.entity.QNotice;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.Projections;
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
            Boolean lastIsImportant,
            Pageable pageable
    ) {
        BooleanBuilder whereBuilder = new BooleanBuilder();

        // 커서 조건 (isImportant → createdAt → id)
        if (lastNoticeId != null && lastCreatedAt != null && lastIsImportant != null) {
            whereBuilder.and(
                    n.isImportant.lt(lastIsImportant)
                            .or(n.isImportant.eq(lastIsImportant)
                                    .and(n.createdAt.lt(lastCreatedAt))
                            )
                            .or(n.isImportant.eq(lastIsImportant)
                                    .and(n.createdAt.eq(lastCreatedAt))
                                    .and(n.id.lt(lastNoticeId))
                            )
            );
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
                .orderBy(
                        n.isImportant.desc(),   // 중요공지 먼저
                        n.createdAt.desc(),     // 최신순
                        n.id.desc()             // 동일 시간대는 ID 순
                )
                .limit(pageable.getPageSize() + 1) // 다음 페이지 여부 확인용
                .fetch();

        boolean hasNext = results.size() > pageable.getPageSize();
        if (hasNext) {
            results.remove(results.size() - 1);
        }

        return new PagingNoticeListResDto(results, hasNext);
    }

    @Override
    public Notice findPreviousNotice(Long noticeId) {
        // 현재 notice의 중요도 및 생성일을 먼저 가져오기
        Notice current = queryFactory
                .selectFrom(n)
                .where(n.id.eq(noticeId))
                .fetchOne();

        if (current == null) return null;

        // 동일한 정렬 규칙: 중요공지 → 최신순
        return queryFactory
                .selectFrom(n)
                .where(
                        // 1) 같은 중요도 내에서 createdAt이 더 이전이거나
                        (n.isImportant.eq(current.isImportant())
                                .and(n.createdAt.lt(current.getCreatedAt())))
                                // 2) 혹은 중요도가 낮은 게시글
                                .or(n.isImportant.lt(current.isImportant()))
                )
                .orderBy(n.isImportant.desc(), n.createdAt.desc())
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
                        // 1) 같은 중요도 내에서 createdAt이 더 최신이거나
                        (n.isImportant.eq(current.isImportant())
                                .and(n.createdAt.gt(current.getCreatedAt())))
                                // 2) 혹은 중요도가 높은 게시글
                                .or(n.isImportant.gt(current.isImportant()))
                )
                .orderBy(n.isImportant.asc(), n.createdAt.asc())
                .fetchFirst();
    }
}
