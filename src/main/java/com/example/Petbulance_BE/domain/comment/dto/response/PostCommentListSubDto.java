package com.example.Petbulance_BE.domain.comment.dto.response;

import com.querydsl.core.annotations.QueryProjection;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class PostCommentListSubDto {
    private Long commentId;
    private Long parentId;
    private String writerNickname;
    private String writerProfileUrl;
    private String mentionUserNickname;
    private String content;
    private boolean isSecret;
    private boolean deleted;
    private boolean hidden;
    private String imageUrl;
    private String writerId; // 댓글작성자
    private LocalDateTime createdAt;
}
