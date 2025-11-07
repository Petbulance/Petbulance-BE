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
    private Long boardId;
    private Long postId;
    private String postTitle;
    private String commentContent;
    private String createdAt;
    private boolean hidden;

    public MyCommentListResDto(Long commentId, Long boardId, Long postId, String postTitle, String commentContent, LocalDateTime createdAt, boolean hidden) {
        this.commentId = commentId;
        this.boardId = boardId;
        this.postId = postId;
        this.postTitle = postTitle;
        this.commentContent = commentContent;
        this.createdAt = TimeUtil.formatCreatedAt(createdAt);
        this.hidden = hidden;
    }
}
