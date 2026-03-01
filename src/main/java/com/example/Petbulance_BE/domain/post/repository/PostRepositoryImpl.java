package com.example.Petbulance_BE.domain.post.repository;

import com.example.Petbulance_BE.domain.board.entity.QBoard;
import com.example.Petbulance_BE.domain.comment.entity.QPostCommentCount;
import com.example.Petbulance_BE.domain.post.dto.response.*;
import com.example.Petbulance_BE.domain.post.entity.*;
import com.example.Petbulance_BE.domain.post.type.Topic;
import com.example.Petbulance_BE.domain.user.entity.QUsers;
import com.example.Petbulance_BE.domain.user.entity.Users;
import com.example.Petbulance_BE.global.common.error.exception.CustomException;
import com.example.Petbulance_BE.global.common.error.exception.ErrorCode;
import com.example.Petbulance_BE.global.common.type.AnimalType;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.jpa.JPAExpressions;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
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


    private List<DetailPostResDto.ImageInfo> fetchImagesByPostId(Long postId) {
        QPostImage pi = QPostImage.postImage;

        return queryFactory
                .select(Projections.constructor(
                        DetailPostResDto.ImageInfo.class,
                        pi.id,
                        pi.imageUrl,
                        pi.imageOrder,
                        pi.thumbnail
                ))
                .from(pi)
                .where(pi.post.id.eq(postId))
                .orderBy(pi.imageOrder.asc())
                .fetch();
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
    public Slice<PostListResDto> findPostList(
            AnimalType type,
            Topic topic,
            String sort,
            Long lastPostId,
            Integer pageSize
    ) {
        QPost p = QPost.post;
        QPostLikeCount like = QPostLikeCount.postLikeCount1;
        QPostCommentCount comment = QPostCommentCount.postCommentCount1;
        QBoard b = QBoard.board;
        QPostImage img = QPostImage.postImage;

        JPAQuery<PostListResDto> query = queryFactory
                .select(Projections.constructor(
                        PostListResDto.class,
                        p.id,
                        p.animalType,
                        p.topic,
                        p.createdAt,
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

                .leftJoin(like).on(like.postId.eq(p.id))
                .leftJoin(comment).on(comment.postId.eq(p.id))
                .leftJoin(img).on(
                        img.post.id.eq(p.id)
                                .and(img.thumbnail.isTrue())
                )
                .where(
                        p.deleted.isFalse(),
                        p.hidden.isFalse()
                );

        if (type != null) {
            query.where(p.animalType.eq(type));
        }

        if (topic != null) {
            query.where(p.topic.eq(topic));
        }

        if (lastPostId != null) {
            query.where(p.id.lt(lastPostId));
        }

        if ("popular".equals(sort)) {
            query.orderBy(
                    like.postLikeCount.desc().nullsLast(),
                    p.createdAt.desc()
            );
        } else if ("comment".equals(sort)) {
            query.orderBy(
                    comment.postCommentCount.desc().nullsLast(),
                    p.createdAt.desc()
            );
        } else {
            query.orderBy(p.createdAt.desc());
        }

        List<PostListResDto> results = query
                .limit(pageSize + 1)
                .fetch();

        boolean hasNext = results.size() > pageSize;
        if (hasNext) {
            results.remove(pageSize);
        }

        return new SliceImpl<>(results, PageRequest.of(0, pageSize), hasNext);
    }


    @Override
    public PagingPostSearchListResDto findPostSearchList(AnimalType type, Topic topic, String sort, Long lastPostId, Integer pageSize, String searchKeyword, String searchScope) {
        QPost p = QPost.post;
        QPostLikeCount like = QPostLikeCount.postLikeCount1;
        QPostCommentCount comment = QPostCommentCount.postCommentCount1;

        QUsers u = QUsers.users;
        QPostImage img = QPostImage.postImage;

        BooleanBuilder condition = new BooleanBuilder();
        condition.and(p.deleted.isFalse()).and(p.hidden.isFalse()); // 목록 조회시 삭제되고 숨겨진 게시글은 제외

        if (type != null) {
            condition.and(p.animalType.eq(type));
        }

        if (topic != null) {
            condition.and(p.topic.eq(topic));
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
                        p.animalType,
                        p.topic,
                        u.nickname,
                        p.createdAt,
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

        return new PagingPostSearchListResDto(
                new SliceImpl<>(results, PageRequest.of(0, pageSize), hasNext)
        );
    }

    @Override
    public PagingMyPostListResDto findMyPostList(Users currentUser, String keyword, Long lastPostId, Pageable pageable) {
        QPost p = QPost.post;
        QPostImage pi = QPostImage.postImage;
        QPostLikeCount pl = new QPostLikeCount("postLike");


        BooleanBuilder whereBuilder = new BooleanBuilder();
        whereBuilder.and(p.user.eq(currentUser))
                .and(p.deleted.eq(false));

        if (lastPostId != null) {
            whereBuilder.and(p.id.lt(lastPostId));
        }

        if (keyword != null && !keyword.isBlank()) {
            whereBuilder.and(
                    p.title.containsIgnoreCase(keyword)
                            .or(p.content.containsIgnoreCase(keyword))
            );
        }

        List<MyPostListResDto> results = queryFactory
                .select(Projections.constructor(
                        MyPostListResDto.class,
                        p.id,
                        p.title,
                        p.content,
                        p.createdAt,
                        Expressions.constant(0L),
                        JPAExpressions
                                .select(pl.postLikeCount)
                                .from(pl)
                                .where(pl.postId.eq(p.id))
                                .limit(1),
                        JPAExpressions
                                .select(pi.imageUrl)
                                .from(pi)
                                .where(
                                        pi.post.eq(p),
                                        pi.thumbnail.eq(true)
                                )
                                .limit(1),

                        p.hidden
                ))
                .from(p)
                .where(whereBuilder)
                .orderBy(p.id.desc())
                .limit(pageable.getPageSize() + 1)
                .fetch();

        boolean hasNext = results.size() > pageable.getPageSize();
        if (hasNext) {
            results.remove(results.size() - 1);
        }

        return new PagingMyPostListResDto(results, hasNext);
    }



}
