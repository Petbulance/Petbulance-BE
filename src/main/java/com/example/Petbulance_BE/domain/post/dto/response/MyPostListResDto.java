package com.example.Petbulance_BE.domain.post.dto.response;

import com.example.Petbulance_BE.global.util.TimeUtil;
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
    private String createdAt;
    private Long viewCount;
    private boolean hidden;

    public MyPostListResDto(Long postId, Long boardId, String title, String content, LocalDateTime createdAt, Long viewCount, boolean hidden) {
        this.postId = postId;
        this.boardId = boardId;
        this.title = title;
        this.content = content;
        this.createdAt = TimeUtil.formatCreatedAt(createdAt);
        this.viewCount = viewCount;
        this.hidden = hidden;
    }
}
