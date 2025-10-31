package com.example.Petbulance_BE.domain.post.repository;

import com.example.Petbulance_BE.domain.board.entity.QBoard;
import com.example.Petbulance_BE.domain.comment.entity.QPostCommentCount;
import com.example.Petbulance_BE.domain.post.dto.response.InquiryPostResDto;
import com.example.Petbulance_BE.domain.post.dto.response.PagingPostSearchListResDto;
import com.example.Petbulance_BE.domain.post.dto.response.PostListResDto;
import com.example.Petbulance_BE.domain.post.dto.response.PostSearchListResDto;
import com.example.Petbulance_BE.domain.post.entity.*;
import com.example.Petbulance_BE.domain.post.type.Category;
import com.example.Petbulance_BE.domain.user.entity.QUsers;
import com.example.Petbulance_BE.domain.user.entity.Users;
import com.example.Petbulance_BE.global.common.error.exception.CustomException;
import com.example.Petbulance_BE.global.common.error.exception.ErrorCode;
import com.example.Petbulance_BE.global.util.TimeUtil;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.SliceImpl;
import org.springframework.stereotype.Repository;

import java.util.List;

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

        // 정적 데이터만 조회
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
                                p.createdAt.stringValue(),
                                p.content,
                                Expressions.stringTemplate("GROUP_CONCAT({0} ORDER BY {1} ASC)", pi.imageUrl, pi.imageOrder),
                                // 동적 데이터는 0이나 기본값으로 처리
                                Expressions.constant(0),
                                Expressions.constant(0),
                                Expressions.constant(0),
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

    @Override
    public Slice<PostListResDto> findPostList(Long boardId, Category c, String sort, Long lastPostId, Integer pageSize) {
        QPost p = QPost.post;
        QPostLikeCount like = QPostLikeCount.postLikeCount1;
        QPostCommentCount comment = QPostCommentCount.postCommentCount1;
        QBoard b = QBoard.board;
        QUsers u = QUsers.users;
        QPostImage img = QPostImage.postImage;

        JPAQuery<PostListResDto> query = queryFactory
                .select(Projections.constructor(
                        PostListResDto.class,
                        p.id,
                        b.id,
                        b.nameKr,
                        p.category.stringValue(),
                        u.profileImage,
                        u.nickname,
                        Expressions.constant(p.createdAt),
                        img.imageUrl,
                        p.imageNum.longValue(),
                        p.title,
                        p.content,
                        like.postLikeCount.coalesce(0L), // postLikeCount가 null이면 0으로 대체
                        comment.postCommentCount.coalesce(0L),
                        Expressions.constant(0L), // 서비스에서 매핑
                        Expressions.constant(false) // 서비스에서 매핑
                ))
                .from(p)
                .leftJoin(p.board, b)
                .leftJoin(p.user, u)
                .leftJoin(like).on(like.postId.eq(p.id))
                .leftJoin(comment).on(comment.postId.eq(p.id))
                .leftJoin(img).on(img.post.id.eq(p.id).and(img.thumbnail.isTrue()))
                .where(p.deleted.isFalse(), p.hidden.isFalse());

        if (boardId != null) {
            query.where(p.board.id.eq(boardId));
        }

        if (c != null) {
            query.where(p.category.eq(c));
        }

        if (lastPostId != null) {
            query.where(p.id.lt(lastPostId));
        }

        if ("popular".equals(sort)) { // 인기순 - 좋아요순 정렬
            query.orderBy(like.postLikeCount.desc().nullsLast(), p.createdAt.desc());
        } else if ("comment".equals(sort)) { // 댓글순 - 댓글수 정렬
            query.orderBy(comment.postCommentCount.desc().nullsLast(), p.createdAt.desc());
        } else { // default: 최신순
            query.orderBy(p.createdAt.desc());
        }

        List<PostListResDto> results = query
                .limit(pageSize + 1)
                .fetch();

        boolean hasNext = results.size() > pageSize;
        if (hasNext) results.remove(pageSize);

        return new SliceImpl<>(results, PageRequest.of(0, pageSize), hasNext);
    }

    @Override
    public PagingPostSearchListResDto findPostSearchList(Long boardId, List<String> category, String sort, Long lastPostId, Integer pageSize, String searchKeyword, String searchScope) {
        QPost p = QPost.post;
        QPostLikeCount like = QPostLikeCount.postLikeCount1;
        QPostCommentCount comment = QPostCommentCount.postCommentCount1;
        QBoard b = QBoard.board;
        QUsers u = QUsers.users;
        QPostImage img = QPostImage.postImage;

        BooleanBuilder condition = new BooleanBuilder();
        condition.and(p.deleted.isFalse()).and(p.hidden.isFalse()); // 목록 조회시 삭제되고 숨겨진 게시글은 제외

        if (boardId != null) {
            condition.and(p.board.id.eq(boardId));
        }

        if (category != null && !category.isEmpty()) {
            condition.and(p.category.stringValue().in(category));
        }

        if (searchKeyword != null && !searchKeyword.isBlank()) {
            BooleanBuilder searchBuilder = new BooleanBuilder();
            switch (searchScope.toLowerCase()) {
                case "title_content":
                    searchBuilder.or(p.title.containsIgnoreCase(searchKeyword))
                            .or(p.content.containsIgnoreCase(searchKeyword));
                    break;
                case "title":
                    searchBuilder.or(p.title.containsIgnoreCase(searchKeyword));
                    break;
                case "writer":
                    searchBuilder.or(u.nickname.containsIgnoreCase(searchKeyword));
                    break;
                default:
                    throw new CustomException(ErrorCode.INVALID_SEARCH_SCOPE);
            }
            condition.and(searchBuilder);
        }

        if (lastPostId != null) {
            condition.and(p.id.lt(lastPostId));
        }

        JPAQuery<PostSearchListResDto> dataQuery = queryFactory
                .select(Projections.constructor(
                        PostSearchListResDto.class,
                        p.id,
                        b.id,
                        b.nameKr,
                        Expressions.list(p.category.stringValue()),
                        u.profileImage,
                        u.nickname,
                        p.createdAt.stringValue(),
                        img.imageUrl,
                        p.imageNum.longValue(),
                        p.title,
                        p.content,
                        like.postLikeCount.coalesce(0L),
                        comment.postCommentCount.coalesce(0L),
                        Expressions.constant(0L),
                        Expressions.constant(false)
                ))
                .from(p)
                .leftJoin(p.board, b)
                .leftJoin(p.user, u)
                .leftJoin(like).on(like.postId.eq(p.id))
                .leftJoin(comment).on(comment.postId.eq(p.id))
                .leftJoin(img).on(img.post.id.eq(p.id).and(img.thumbnail.isTrue()))
                .where(condition);

        if ("popular".equalsIgnoreCase(sort)) {
            dataQuery.orderBy(like.postLikeCount.desc().nullsLast(), p.createdAt.desc());
        } else if ("comment".equalsIgnoreCase(sort)) {
            dataQuery.orderBy(comment.postCommentCount.desc().nullsLast(), p.createdAt.desc());
        } else {
            dataQuery.orderBy(p.createdAt.desc());
        }

        List<PostSearchListResDto> results = dataQuery
                .limit(pageSize + 1)
                .fetch();

        boolean hasNext = results.size() > pageSize;
        if (hasNext) results.remove(pageSize);

        Long totalCount = queryFactory
                .select(p.count())
                .from(p)
                .leftJoin(p.user, u)
                .where(condition)
                .fetchOne();

        return new PagingPostSearchListResDto(
                new SliceImpl<>(results, PageRequest.of(0, pageSize), hasNext),
                totalCount != null ? totalCount : 0L
        );
    }

}
