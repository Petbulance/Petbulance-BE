package com.example.Petbulance_BE.domain.report.repository;

import com.example.Petbulance_BE.domain.comment.entity.QPostComment;
import com.example.Petbulance_BE.domain.post.entity.QPost;
import com.example.Petbulance_BE.domain.report.dto.response.PagingReportListResDto;
import com.example.Petbulance_BE.domain.report.dto.response.ReportListResDto;

import com.example.Petbulance_BE.domain.report.entity.QReport;
import com.example.Petbulance_BE.domain.report.entity.Report;
import com.example.Petbulance_BE.domain.report.type.ReportType;
import com.example.Petbulance_BE.domain.user.entity.QUsers;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.CaseBuilder;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
@RequiredArgsConstructor
public class ReportRepositoryImpl implements ReportRepositoryCustom {

    private final JPAQueryFactory queryFactory;

    @Override
    public PagingReportListResDto findPagingReports(int page, int size) {
        QReport report = QReport.report;
        QPost post = QPost.post;
        QPostComment comment = QPostComment.postComment;
        QUsers postUser = QUsers.users;
        QUsers commentUser = QUsers.users;

        long offset = (long) (page - 1) * size;

        List<Long> ids = queryFactory
                .select(report.reportId)
                .from(report)
                .orderBy(report.reportId.desc())
                .offset(offset)
                .limit(size)
                .fetch();

        System.out.println("ids: " +  ids);

        if (ids.isEmpty()) {
            return new PagingReportListResDto(
                    List.of(), page, size, 0, 0, false, page > 1
            );
        }

        List<ReportListResDto> content = queryFactory
                .select(
                        Projections.constructor(
                                ReportListResDto.class,
                                report.reportId,
                                report.reportType,

                                Projections.constructor(
                                        ReportListResDto.PostDto.class,
                                        post.title,
                                        postUser.nickname,
                                        post.createdAt
                                ),

                                Projections.constructor(
                                        ReportListResDto.CommentDto.class,
                                        comment.content,
                                        commentUser.nickname,
                                        comment.createdAt
                                ),

                                report.createdAt,
                                report.status,
                                report.actionType
                        )
                )
                .from(report)
                .leftJoin(post).on(report.postId.eq(post.id))
                .leftJoin(postUser).on(post.user.id.eq(postUser.id))
                .leftJoin(comment).on(report.commentId.eq(comment.id))
                .leftJoin(commentUser).on(comment.user.id.eq(commentUser.id))
                .where(report.reportId.in(ids))
                .orderBy(report.reportId.desc())
                .fetch();





        long totalElements = queryFactory
                .select(report.reportId.count())
                .from(report)
                .fetchOne();

        int totalPages = (int) Math.ceil((double) totalElements / size);

        boolean hasNext = page < totalPages;
        boolean hasPrev = page > 1;

        return new PagingReportListResDto(
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
