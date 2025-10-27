package com.example.Petbulance_BE.domain.post.repository;

import com.example.Petbulance_BE.domain.board.entity.QBoard;
import com.example.Petbulance_BE.domain.comment.entity.QPostCommentCount;
import com.example.Petbulance_BE.domain.post.dto.response.InquiryPostResDto;
import com.example.Petbulance_BE.domain.post.entity.*;
import com.example.Petbulance_BE.domain.user.entity.QUsers;
import com.example.Petbulance_BE.domain.user.entity.Users;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

/**
 * 게시글 상세 조회 (정적 + 실시간 데이터 분리)
 * 정적 데이터: 게시판/작성자/이미지 등 자주 변하지 않는 정보
 * 실시간 데이터: 좋아요수, 댓글수, 조회수, 사용자 좋아요 여부
 */
@Repository
@RequiredArgsConstructor
public class PostRepositoryImpl implements PostRepositoryCustom {

    private final JPAQueryFactory queryFactory;

    @Override
    public InquiryPostResDto findInquiryPost(Post post, boolean currentUserIsPostAuthor,
                                             Users currentUser, Long viewCount) {
        QPost p = QPost.post;
        QBoard b = QBoard.board;
        QUsers u = QUsers.users;
        QPostImage pi = QPostImage.postImage;

        // 정적 데이터 조회 (캐시 대상)
        InquiryPostResDto cached = queryFactory
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
                                p.createdAt.stringValue(),
                                p.content,
                                Expressions.stringTemplate("GROUP_CONCAT({0} ORDER BY {1} ASC)", pi.imageUrl, pi.imageOrder),
                                Expressions.constant(0),
                                Expressions.constant(0),
                                Expressions.constant(viewCount),
                                Expressions.constant(false),
                                Expressions.constant(currentUserIsPostAuthor)
                        )
                ))
                .from(p)
                .join(p.board, b)
                .join(p.user, u)
                .leftJoin(pi).on(pi.post.eq(p))
                .where(p.eq(post), p.deleted.eq(false))
                .groupBy(p.id, b.id, u.id)
                .fetchOne();

        if (cached == null) return null;

        Long postId = post.getId();

        InquiryPostResDto.PostInfo postInfo = cached.getPost().toBuilder()
                .likeCount(fetchLikeCount(postId))
                .commentCount(fetchCommentCount(postId))
                .viewCount(viewCount.intValue())
                .likedByUser(fetchLikedByUser(currentUser, postId))
                .isCurrentUserPost(currentUserIsPostAuthor)
                .build();

        return InquiryPostResDto.builder()
                .board(cached.getBoard())
                .post(postInfo)
                .build();
    }

    public int fetchLikeCount(Long postId) {
        QPostLikeCount plc = QPostLikeCount.postLikeCount1;
        Long count = queryFactory
                .select(plc.postLikeCount.coalesce(0L))
                .from(plc)
                .where(plc.postId.eq(postId))
                .fetchOne();
        return count != null ? count.intValue() : 0;
    }

    public int fetchCommentCount(Long postId) {
        QPostCommentCount pcc = QPostCommentCount.postCommentCount1;
        Long count = queryFactory
                .select(pcc.postCommentCount.coalesce(0L))
                .from(pcc)
                .where(pcc.postId.eq(postId))
                .fetchOne();
        return count != null ? count.intValue() : 0;
    }

    public boolean fetchLikedByUser(Users currentUser, Long postId) {
        if (currentUser == null) return false;
        QPostLike pl = QPostLike.postLike;
        return queryFactory
                .selectFrom(pl)
                .where(pl.post.id.eq(postId), pl.user.eq(currentUser))
                .fetchFirst() != null;
    }
}
