package com.example.Petbulance_BE.domain.report.repository;

import com.example.Petbulance_BE.domain.comment.entity.QPostComment;
import com.example.Petbulance_BE.domain.post.entity.QPost;
import com.example.Petbulance_BE.domain.report.dto.response.PagingReportListResDto;
import com.example.Petbulance_BE.domain.report.dto.response.ReportListResDto;

import com.example.Petbulance_BE.domain.report.entity.QReport;
import com.example.Petbulance_BE.domain.report.type.ReportType;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.CaseBuilder;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class ReportRepositoryImpl implements ReportRepositoryCustom {

    private final JPAQueryFactory queryFactory;

    @Override
    public PagingReportListResDto reportList(Long lastReportId, Integer pageSize) {

        QReport r = QReport.report;
        QPost p = QPost.post;
        QPostComment c = QPostComment.postComment;

        List<ReportListResDto> rows = queryFactory
                .select(Projections.constructor(
                        ReportListResDto.class,
                        r.reportId,

                        /* 신고 유형 */
                        new CaseBuilder()
                                .when(r.postId.isNotNull()).then(ReportType.POST)
                                .when(r.commentId.isNotNull()).then(ReportType.COMMENT)
                                .otherwise(ReportType.USER),

                        /* content */
                        new CaseBuilder()
                                .when(r.postId.isNotNull()).then(p.content)
                                .when(r.commentId.isNotNull()).then(c.content)
                                .otherwise((String) null),

                        r.postId,
                        r.commentId,
                        r.reportReason,
                        r.reporter.nickname,
                        r.targetUser.nickname,
                        r.status,
                        r.actionType,
                        r.createdAt
                ))
                .from(r)
                .leftJoin(p).on(r.postId.eq(p.id))
                .leftJoin(c).on(r.commentId.eq(c.id))
                .where(ltReportId(lastReportId, r))
                .orderBy(r.reportId.desc())
                .limit(pageSize + 1)
                .fetch();

        boolean hasNext = rows.size() > pageSize;
        if (hasNext) {
            rows = rows.subList(0, pageSize);
        }

        return new PagingReportListResDto(rows, hasNext);
    }

    private BooleanExpression ltReportId(Long lastReportId, QReport r) {
        return lastReportId == null ? null : r.reportId.lt(lastReportId);
    }
}
