package com.example.Petbulance_BE.domain.comment.dto.response;

import com.example.Petbulance_BE.domain.comment.entity.PostComment;
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
        dto.parentId = (postComment.getParent() != null)
                ? postComment.getParent().getId()
                : null;
        dto.mentionUserNickname = (postComment.getMentionUser() != null)
                ? postComment.getMentionUser().getNickname()
                : null;
        dto.isSecret = postComment.getIsSecret();
        dto.imageUrl = postComment.getImageUrl();
        dto.createdAt = postComment.getCreatedAt();
        return dto;
    }
}
