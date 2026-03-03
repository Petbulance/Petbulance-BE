package com.example.Petbulance_BE.domain.notification.repository;

import com.example.Petbulance_BE.domain.comment.entity.QPostComment;
import com.example.Petbulance_BE.domain.notification.dto.response.NotificationListResDto;
import com.example.Petbulance_BE.domain.notification.entity.QNotification;
import com.example.Petbulance_BE.domain.notification.type.NotificationTargetType;
import com.example.Petbulance_BE.domain.post.entity.QPost;
import com.example.Petbulance_BE.domain.post.type.Topic;
import com.example.Petbulance_BE.domain.report.entity.QReport;
import com.example.Petbulance_BE.global.common.type.AnimalType;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.Coalesce;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class NotificationRepositoryImpl implements NotificationRepositoryCustom {

    private final JPAQueryFactory queryFactory;

    private final QNotification notification = QNotification.notification;

    @Override
    public Slice<NotificationListResDto> fetchMyNotificationSlice(String receiverId, Long lastNotificationId, int size) {

        QPost postFromPostTarget = new QPost("postFromPostTarget");              // targetType=POST
        QPost postFromCommentTarget = new QPost("postFromCommentTarget");        // targetType=COMMENT -> comment.post
        QPost postFromReportPostId = new QPost("postFromReportPostId");          // targetType=SANCTION -> report.postId
        QPost postFromReportComment = new QPost("postFromReportComment");        // targetType=SANCTION -> report.commentId -> comment.post

        QPostComment commentTarget = new QPostComment("commentTarget");          // COMMENT 대상
        QPostComment reportCommentTarget = new QPostComment("reportCommentTarget"); // SANCTION(comment 신고) 대상

        QReport report = QReport.report;

        BooleanBuilder where = new BooleanBuilder()
                .and(notification.receiver.id.eq(receiverId));

        if (lastNotificationId != null) {
            where.and(notification.id.lt(lastNotificationId));
        }

        Coalesce<AnimalType> animalTypeExpr = new Coalesce<AnimalType>()
                .add(postFromPostTarget.animalType)
                .add(postFromCommentTarget.animalType)
                .add(postFromReportPostId.animalType)
                .add(postFromReportComment.animalType);

        Coalesce<Topic> topicExpr = new Coalesce<Topic>()
                .add(postFromPostTarget.topic)
                .add(postFromCommentTarget.topic)
                .add(postFromReportPostId.topic)
                .add(postFromReportComment.topic);

        List<NotificationListResDto> rows = queryFactory
                .select(Projections.constructor(
                        NotificationListResDto.class,
                        notification.id,
                        animalTypeExpr,
                        topicExpr,
                        notification.createdAt,
                        notification.message,
                        notification.read
                ))
                .from(notification)

                // 1) POST 타겟: targetId = postId
                .leftJoin(postFromPostTarget).on(
                        notification.targetType.eq(NotificationTargetType.POST)
                                .and(postFromPostTarget.id.eq(notification.targetId))
                )

                // 2) COMMENT 타겟: targetId = postCommentId -> comment.post
                .leftJoin(commentTarget).on(
                        notification.targetType.eq(NotificationTargetType.COMMENT)
                                .and(commentTarget.id.eq(notification.targetId))
                )
                .leftJoin(postFromCommentTarget).on(
                        postFromCommentTarget.id.eq(commentTarget.post.id)
                )

                // 3) SANCTION 타겟: targetId = reportId
                .leftJoin(report).on(
                        notification.targetType.eq(NotificationTargetType.SANCTION)
                                .and(report.reportId.eq(notification.targetId))
                )
                // 3-1) report.postId -> post
                .leftJoin(postFromReportPostId).on(
                        postFromReportPostId.id.eq(report.postId)
                )
                // 3-2) report.commentId -> comment -> post
                .leftJoin(reportCommentTarget).on(
                        reportCommentTarget.id.eq(report.commentId)
                )
                .leftJoin(postFromReportComment).on(
                        postFromReportComment.id.eq(reportCommentTarget.post.id)
                )

                .where(where)
                .orderBy(notification.id.desc())
                .limit(size + 1L)
                .fetch();

        boolean hasNext = rows.size() > size;
        if (hasNext) rows = rows.subList(0, size);

        return new SliceImpl<>(rows, PageRequest.of(0, size), hasNext);
    }
}