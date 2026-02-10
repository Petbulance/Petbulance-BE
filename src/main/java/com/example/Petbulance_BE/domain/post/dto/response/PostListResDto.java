package com.example.Petbulance_BE.domain.post.dto.response;

import com.example.Petbulance_BE.domain.post.type.Category;
import com.example.Petbulance_BE.global.util.TimeUtil;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PostListResDto {
    private Long postId;
    private Long boardId;
    private String boardName;
    private Category category;
    private String createdAt;
    private String thumbnailUrl;
    private Long imageCount;
    private String title;
    private String content;
    private Long likeCount;
    private Long commentCount;
    private Long viewCount;
    private boolean likedByUser;

    public PostListResDto(Long postId, Long boardId, String boardName, Category category, LocalDateTime createdAt, String thumbnailUrl, Long imageCount, String title, String content, Long likeCount, Long commentCount, Long viewCount, boolean likedByUser) {
        this.postId = postId;
        this.boardId = boardId;
        this.boardName = boardName;
        this.category = category;
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
