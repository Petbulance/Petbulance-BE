package com.example.Petbulance_BE.domain.post.dto.response;

import com.example.Petbulance_BE.domain.comment.entity.PostComment;
import com.example.Petbulance_BE.domain.comment.entity.PostCommentCount;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class PostCommentResDto {
    private Long commentId;
    private String content;
    private Long parentId;
    private String mentionUserNickname;
    private boolean isSecret;
    private String imageUrl;
    private LocalDateTime createdAt;

    public static PostCommentResDto of(PostComment postComment) {
        PostCommentResDto dto = new PostCommentResDto();
        dto.commentId = postComment.getId();
        dto.content = postComment.getContent();
        dto.parentId = postComment.getParent().getId();
        dto.mentionUserNickname = postComment.getMentionUser().getNickname();
        dto.isSecret = postComment.getIsSecret();
        dto.imageUrl = postComment.getImageUrl();
        dto.createdAt = postComment.getCreatedAt();

        return dto;
    }
}
