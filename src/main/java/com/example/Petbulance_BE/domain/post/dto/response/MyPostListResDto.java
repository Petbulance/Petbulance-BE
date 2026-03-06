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
    private String title;
    private String content;
    private String createdAt;
    private Long viewCount;
    private Long likeCount;
    private String thumbnailUrl;
    private boolean hidden;

    public MyPostListResDto(Long postId, String title, String content, LocalDateTime createdAt, Long viewCount, Long likeCount, String thumbnailUrl, boolean hidden) {
        this.postId = postId;
        this.title = title;
        this.content = content;
        this.createdAt = TimeUtil.formatCreatedAt(createdAt);
        this.viewCount = viewCount;
        this.likeCount = likeCount;
        this.thumbnailUrl = thumbnailUrl;
        this.hidden = hidden;
    }
}
