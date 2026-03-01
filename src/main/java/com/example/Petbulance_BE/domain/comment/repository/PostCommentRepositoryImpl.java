package com.example.Petbulance_BE.domain.comment.repository;

import com.example.Petbulance_BE.domain.board.entity.QBoard;
import com.example.Petbulance_BE.domain.comment.dto.response.*;
import com.example.Petbulance_BE.domain.comment.entity.QPostComment;
import com.example.Petbulance_BE.domain.post.entity.Post;
import com.example.Petbulance_BE.domain.post.entity.QPost;
import com.example.Petbulance_BE.domain.post.type.Topic;
import com.example.Petbulance_BE.domain.user.entity.QUsers;
import com.example.Petbulance_BE.domain.user.entity.Users;
import com.example.Petbulance_BE.global.common.type.AnimalType;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.SliceImpl;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class PostCommentRepositoryImpl implements PostCommentRepositoryCustom{
    private final JPAQueryFactory queryFactory;

    @Override
    public Slice<PostCommentListResDto> findPostCommentByPost(
            Post post, Long lastParentCommentId, Long lastCommentId, Pageable pageable, boolean currentUserIsPostAuthor, Users currentUser) {

        QPostComment c = QPostComment.postComment;
        QPostComment p = new QPostComment("parent");
        QUsers author = new QUsers("author");
        QUsers mentioned = new QUsers("mentioned");

        BooleanExpression cursorCondition = null;
        if (lastCommentId != null && lastParentCommentId != null) {
            cursorCondition = c.parent.id.gt(lastParentCommentId)
                    .or(
                            c.parent.id.eq(lastParentCommentId)
                                    .and(c.id.gt(lastCommentId))
                    );
        }

        List<PostCommentListSubDto> rows = queryFactory
                .select(Projections.constructor(
                        PostCommentListSubDto.class,
                        c.id,
                        c.parent.id,
                        author.nickname,
                        author.profileImage,
                        mentioned.nickname,
                        c.content,
                        c.isSecret,
                        c.deleted,
                        c.hidden,
                        c.imageUrl,
                        author.id,
                        c.isCommentFromPostAuthor,
                        c.createdAt
                ))
                .from(c)
                .join(c.user, author)
                .leftJoin(c.mentionUser, mentioned)
                .leftJoin(c.parent, p)
                .where(
                        c.post.eq(post),
                       cursorCondition
                )
                .orderBy(c.parent.id.asc(), c.id.asc())
                .limit(pageable.getPageSize() + 1)
                .fetch();

        // Slice 구성
        boolean hasNext = rows.size() > pageable.getPageSize();
        if (hasNext) rows = rows.subList(0, pageable.getPageSize());

        List<PostCommentListResDto> content = rows.stream()
                .map(o -> PostCommentListResDto.of(o, currentUserIsPostAuthor, currentUser))
                .toList();

        return new SliceImpl<>(content, pageable, hasNext);
    }

    @Override
    public Slice<PostCommentListResDto> findPostCommentByPostForGuest(
            Post post, Long lastParentCommentId, Long lastCommentId, Pageable pageable) {

        QPostComment c = QPostComment.postComment;
        QPostComment p = new QPostComment("parent");
        QUsers author = new QUsers("author");
        QUsers mentioned = new QUsers("mentioned");

        BooleanExpression cursorCondition = null;
        if (lastCommentId != null && lastParentCommentId != null) {
            cursorCondition = c.parent.id.gt(lastParentCommentId)
                    .or(
                            c.parent.id.eq(lastParentCommentId)
                                    .and(c.id.gt(lastCommentId))
                    );
        }

        BooleanExpression visibleCondition = c.deleted.eq(false)
                .and(c.hidden.eq(false))
                .and(c.isSecret.eq(false));

        List<PostCommentListSubDto> rows = queryFactory
                .select(Projections.constructor(
                        PostCommentListSubDto.class,
                        c.id,
                        c.parent.id,
                        author.nickname,
                        author.profileImage,
                        mentioned.nickname,
                        c.content,
                        c.isSecret,
                        c.deleted,
                        c.hidden,
                        c.imageUrl,
                        author.id,
                        c.isCommentFromPostAuthor,
                        c.createdAt
                ))
                .from(c)
                .join(c.user, author)
                .leftJoin(c.mentionUser, mentioned)
                .leftJoin(c.parent, p)
                .where(
                        c.post.eq(post),
                        visibleCondition,
                        cursorCondition
                )
                .orderBy(c.parent.id.asc(), c.id.asc())
                .limit(pageable.getPageSize() + 1)
                .fetch();

        boolean hasNext = rows.size() > pageable.getPageSize();
        if (hasNext) rows = rows.subList(0, pageable.getPageSize());

        List<PostCommentListResDto> content = rows.stream()
                .map(PostCommentListResDto::ofForGuest)
                .toList();

        return new SliceImpl<>(content, pageable, hasNext);
    }

    @Override
    public Slice<SearchPostCommentResDto> findSearchPostComment(String keyword, String searchScope, Long lastCommentId, Integer pageSize, Topic topic, AnimalType type) {
        QPostComment c = QPostComment.postComment;
        QPost p = QPost.post;
        QUsers u = QUsers.users;

        BooleanExpression scopeCond = scopeCondition(searchScope, keyword, c, u);
        BooleanExpression topicCond = (topic != null)
                ? p.topic.eq(topic)
                : null;
        BooleanExpression typeCond = (type != null)
                ? p.animalType.eq(type)
                : null;
        BooleanExpression cursorCond = (lastCommentId != null)
                ? c.id.lt(lastCommentId)
                : null;

        List<SearchPostCommentResDto> rows = queryFactory
                .select(Projections.constructor(
                        SearchPostCommentResDto.class,
                        c.id,
                        p.id,
                        p.title,
                        c.content,
                        c.imageUrl,
                        u.nickname,
                        c.createdAt,
                        p.animalType,
                        p.topic
                ))
                .from(c)
                .join(c.post, p)
                .join(c.user, u)
                .where(
                        scopeCond,
                        topicCond,
                        typeCond,
                        cursorCond,
                        c.deleted.eq(false),
                        c.hidden.eq(false)
                )
                .orderBy(c.id.desc())
                .limit(pageSize + 1)
                .fetch();

        boolean hasNext = rows.size() > pageSize;
        if (hasNext) rows = rows.subList(0, pageSize);

        return new SliceImpl<>(rows, Pageable.ofSize(pageSize), hasNext);
    }

    private BooleanExpression scopeCondition(String searchScope,String keyword,QPostComment c,QUsers u) {
        if (searchScope == null || keyword == null) return null;

        return switch (searchScope.toLowerCase()) {
            case "content" -> c.content.containsIgnoreCase(keyword);
            case "writer" -> u.nickname.containsIgnoreCase(keyword);
            default -> null;
        };
    }

    @Override
    public long countSearchPostComment(
            String keyword,
            String searchScope,
            List<Topic> topic,
            Long boardId
    ) {
        QPostComment c = QPostComment.postComment;
        QPost p = QPost.post;
        QBoard b = QBoard.board;
        QUsers u = QUsers.users;

        BooleanExpression scopeCond = scopeCondition(searchScope, keyword, c, u);
        BooleanExpression categoryCond = (topic != null && !topic.isEmpty())
                ? p.topic.in(topic)
                : null;
        BooleanExpression boardCond = (boardId != null)
                ? b.id.eq(boardId)
                : null;

        return queryFactory
                .select(c.count())
                .from(c)
                .join(c.post, p)
                .join(c.user, u)
                .where(
                        scopeCond,
                        categoryCond,
                        boardCond,
                        c.deleted.eq(false),
                        c.hidden.eq(false)
                )
                .fetchOne();
    }

    @Override
    public PagingMyCommentListResDto findMyCommentList(Users currentUser, String keyword, Long lastCommentId, Pageable pageable) {
        QPostComment pc = QPostComment.postComment;
        QPost p = QPost.post;

        BooleanBuilder whereBuilder = new BooleanBuilder();
        whereBuilder.and(pc.user.id.eq(currentUser.getId()))
                .and(pc.deleted.eq(false)); // 삭제되지 않은 것만

        if(lastCommentId != null) {
            whereBuilder.and(pc.id.lt(lastCommentId));
        }

        if(keyword != null && !keyword.isBlank()) {
            whereBuilder.and(
                    pc.content.containsIgnoreCase(keyword)
            );
        }

        List<MyCommentListResDto> results = queryFactory
                .select(Projections.constructor(
                        MyCommentListResDto.class,
                        pc.id,
                        p.id,
                        p.title,
                        pc.content,
                        pc.createdAt,
                        pc.hidden,
                        pc.isSecret
                ))
                .from(pc)
                .leftJoin(pc.post, p)
                .where(whereBuilder)
                .orderBy(pc.createdAt.desc(), pc.id.desc())
                .limit(pageable.getPageSize() + 1)
                .fetch();

        boolean hasNext = results.size() > pageable.getPageSize();
        if (hasNext) {
            results.remove(results.size() - 1);
        }

        return new PagingMyCommentListResDto(results, hasNext);
    }

}
