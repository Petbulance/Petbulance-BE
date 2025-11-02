package com.example.Petbulance_BE.domain.post.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MyPostListResDto {
    private Long postId;
    private Long boardId;
    private String title;
    private String content;
    private LocalDateTime createdAt;
    private Long viewCount;
    private boolean hidden;
}
