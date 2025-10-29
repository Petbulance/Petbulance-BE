package com.example.Petbulance_BE.domain.post.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PostListResDto {
    private Long postId;
    private Long boardId;
    private String boardName;
    private String category;
    private String writerProfileUrl;
    private String writerNickname;
    private String created;
    private String thumbnailUrl;
    private Long imageCount;
    private String title;
    private String content;
    private Long likeCount;
    private Long commentCount;
    private Long viewCount;
    private boolean likedByUser;
}
