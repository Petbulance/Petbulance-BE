package com.example.Petbulance_BE.domain.notification.repository;

import com.example.Petbulance_BE.domain.comment.entity.QPostComment;
import com.example.Petbulance_BE.domain.notification.dto.response.NotificationListResDto;
import com.example.Petbulance_BE.domain.notification.entity.QNotification;
import com.example.Petbulance_BE.domain.notification.type.NotificationTargetType;
import com.example.Petbulance_BE.domain.post.entity.QPost;
import com.example.Petbulance_BE.domain.post.type.Topic;
import com.example.Petbulance_BE.domain.report.entity.QReport;
import com.example.Petbulance_BE.domain.review.entity.QUserReview;
import com.example.Petbulance_BE.global.common.type.AnimalType;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.Tuple;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.BooleanExpression;
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
    private final QPost post = QPost.post;
    private final QUserReview userReview = QUserReview.userReview; // 리뷰 엔티티 Q클래스

    @Override
    public Slice<NotificationListResDto> fetchMyNotificationSlice(String receiverId, Long lastNotificationId, int size) {

        List<NotificationListResDto> rows = queryFactory
                .select(Projections.constructor(NotificationListResDto.class,
                        notification.id,
                        post.animalType,   // AnimalType
                        post.topic,  // Topic
                        notification.createdAt,
                        notification.message,
                        notification.read,
                        notification.targetType,
                        notification.targetId
                ))
                .from(notification)
                .leftJoin(post).on(
                        notification.targetType.in(NotificationTargetType.POST, NotificationTargetType.COMMENT, NotificationTargetType.SANCTION)
                                .and(notification.targetId.eq(post.id))
                )
                .where(
                        notification.receiver.id.eq(receiverId),
                        ltNotificationId(lastNotificationId) // No-offset 페이징 처리
                )
                .orderBy(notification.id.desc())
                .limit(size + 1) // 다음 페이지 확인을 위해 1개 더 조회
                .fetch();

        boolean hasNext = rows.size() > size;
        if (hasNext) rows.remove(size);

        return new SliceImpl<>(rows, PageRequest.of(0, size), hasNext);
    }

    private BooleanExpression ltNotificationId(Long lastNotificationId) {
        return lastNotificationId == null ? null : notification.id.lt(lastNotificationId);
    }
}