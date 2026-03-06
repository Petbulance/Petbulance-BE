package com.example.Petbulance_BE.domain.comment.dto.response;

import com.example.Petbulance_BE.global.util.TimeUtil;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.cglib.core.Local;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MyCommentListResDto {
    private Long commentId;
    private Long postId;
    private String postTitle;
    private String commentContent;
    private String createdAt;
    private boolean hidden;
    private boolean isSecret;

    public MyCommentListResDto(Long commentId, Long postId, String postTitle, String commentContent, LocalDateTime createdAt, boolean hidden, boolean isSecret) {
        this.commentId = commentId;
        this.postId = postId;
        this.postTitle = postTitle;
        this.commentContent = commentContent;
        this.createdAt = TimeUtil.formatCreatedAt(createdAt);
        this.hidden = hidden;
        this.isSecret = isSecret;
    }
}
