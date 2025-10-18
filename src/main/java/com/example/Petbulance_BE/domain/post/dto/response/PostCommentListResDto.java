package com.example.Petbulance_BE.domain.post.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class PostCommentListResDto {
    private Long commentId;
    private Long parentId;
    private String writerNickname;
    private String writerProfileUrl;
    private String mentionUserNickname;
    private String content;
    private boolean isSecret;
    private boolean isPostAuthor; // 게시글 작성자
    private boolean isCommentAuthor; // 댓글 작성자
    private boolean deleted;
    private boolean hidden;
    private String imageUrl;
    private boolean visibleToUser;
    private LocalDateTime createdAt;
}
