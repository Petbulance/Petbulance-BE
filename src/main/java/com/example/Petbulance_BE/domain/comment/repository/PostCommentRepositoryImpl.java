package com.example.Petbulance_BE.domain.comment.repository;

import com.example.Petbulance_BE.domain.comment.dto.response.PostCommentListResDto;
import com.example.Petbulance_BE.domain.comment.dto.response.PostCommentListSubDto;
import com.example.Petbulance_BE.domain.comment.entity.QPostComment;
import com.example.Petbulance_BE.domain.post.entity.Post;
import com.example.Petbulance_BE.domain.user.entity.QUsers;
import com.example.Petbulance_BE.domain.user.entity.Users;
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
    public Slice<PostCommentListResDto> findPostCommentByPostId(
            Post post, Long lastParentCommentId, Long lastCommentId, Pageable pageable, boolean isPostAuthor, Users currentUser) {

        QPostComment c = QPostComment.postComment;
        QPostComment p = new QPostComment("parent");     // parent 별칭
        QUsers author = new QUsers("author");            // 작성자 별칭
        QUsers mentioned = new QUsers("mentioned");      // 멘션 대상 별칭

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
                        c.id,                    // Long
                        c.parent.id,            // Long (nullable)
                        author.nickname,        // String
                        author.profileImage,    // String
                        mentioned.nickname,     // String (nullable)
                        c.content,              // String
                        c.isSecret,             // Boolean
                        c.deleted,              // Boolean
                        c.hidden,               // Boolean
                        c.imageUrl,             // String
                        author.id,              // String  <-- Users.id가 String이면 SubDto도 String이어야 함
                        c.createdAt             // LocalDateTime
                ))
                .from(c)
                .join(c.user, author)               // fetchJoin() 빼기
                .leftJoin(c.mentionUser, mentioned) // fetchJoin() 빼기
                .leftJoin(c.parent, p)              // fetchJoin() 빼기
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
                .map(o -> PostCommentListResDto.of(o, isPostAuthor, currentUser))
                .toList();

        return new SliceImpl<>(content, pageable, hasNext);
    }

}
