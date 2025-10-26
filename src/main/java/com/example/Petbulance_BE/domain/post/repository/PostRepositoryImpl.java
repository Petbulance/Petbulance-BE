package com.example.Petbulance_BE.domain.post.repository;

import com.example.Petbulance_BE.domain.board.entity.QBoard;
import com.example.Petbulance_BE.domain.comment.entity.QPostCommentCount;
import com.example.Petbulance_BE.domain.post.dto.response.InquiryPostResDto;
import com.example.Petbulance_BE.domain.post.entity.*;
import com.example.Petbulance_BE.domain.user.entity.QUsers;
import com.example.Petbulance_BE.domain.user.entity.Users;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.jpa.JPAExpressions;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class PostRepositoryImpl implements PostRepositoryCustom {

    private final JPAQueryFactory queryFactory;

    @Override
    public InquiryPostResDto findInquiryPost(Post post, boolean currentUserIsPostAuthor, Users currentUser, Long viewCount) {
        QPost p = QPost.post;
        QBoard b = QBoard.board;
        QUsers u = QUsers.users;
        QPostLikeCount plc = QPostLikeCount.postLikeCount1;
        QPostCommentCount pcc = QPostCommentCount.postCommentCount1;
        QPostImage pi = QPostImage.postImage;
        QPostLike pl = QPostLike.postLike;

        BooleanExpression isLikedExpr = JPAExpressions
                .selectOne()
                .from(pl)
                .where(pl.post.eq(p), pl.user.eq(currentUser))
                .exists();

        return queryFactory
                .select(Projections.constructor(
                        InquiryPostResDto.class,
                        Projections.constructor(
                                InquiryPostResDto.BoardInfo.class,
                                b.id,
                                b.nameKr,
                                p.category.stringValue()
                        ),
                        Projections.constructor(
                                InquiryPostResDto.PostInfo.class,
                                p.id,
                                p.title,
                                u.nickname,
                                u.profileImage,
                                // createdAt은 QueryDSL에서 문자열로 변환
                                p.createdAt.stringValue(),
                                p.content,
                                // 🔽 이미지 URL은 별도 쿼리 (join X, N+1 없이 group_concat)
                                Expressions.stringTemplate(
                                        "GROUP_CONCAT({0} ORDER BY {1} ASC)",
                                        pi.imageUrl, pi.imageOrder
                                ),
                                plc.postLikeCount.coalesce(0L),
                                pcc.postCommentCount.coalesce(0L),
                                Expressions.constant(viewCount),
                                isLikedExpr,
                                Expressions.constant(currentUserIsPostAuthor)
                        )
                ))
                .from(p)
                .join(p.board, b)
                .join(p.user, u)
                .leftJoin(plc).on(plc.postId.eq(p.id))
                .leftJoin(pcc).on(pcc.postId.eq(p.id))
                .leftJoin(pi).on(pi.post.eq(p))
                .where(p.eq(post), p.deleted.eq(false))
                .groupBy(p.id, b.id, u.id, plc.postLikeCount, pcc.postCommentCount)
                .fetchOne();
    }
}
