package com.example.Petbulance_BE.domain.post.dto.response;

import com.example.Petbulance_BE.global.util.TimeUtil;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PostSearchListResDto {
    private Long postId;
    private String type;
    private String topic;
    private String writerNickname;
    private String createdAt;
    private String thumbnailUrl;
    private Long imageCount;
    private String title;
    private String content;
    private Long likeCount;
    private Long commentCount;
    private Long viewCount;
    private boolean likedByUser;

    public PostSearchListResDto(Long postId, String type, String topic, String writerNickname, LocalDateTime createdAt, String thumbnailUrl, Long imageCount, String title, String content, Long likeCount, Long commentCount, Long viewCount, boolean likedByUser) {
        this.postId = postId;
        this.type = type;
        this.topic = topic;
        this.writerNickname = writerNickname;
        this.createdAt = TimeUtil.formatCreatedAt(createdAt);
        this.thumbnailUrl = thumbnailUrl;
        this.imageCount = imageCount;
        this.title = title;
        this.content = content;
        this.likeCount = likeCount;
        this.commentCount = commentCount;
        this.viewCount = viewCount;
        this.likedByUser = likedByUser;
    }
}
