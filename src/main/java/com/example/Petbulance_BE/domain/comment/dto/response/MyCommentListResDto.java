package com.example.Petbulance_BE.domain.comment.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

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
    private LocalDateTime createdAt;
    private boolean hidden;
}
