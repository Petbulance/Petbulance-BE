package com.example.Petbulance_BE.domain.post.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PostSearchListResDto {
    private Long postId;
    private Long boardId;
    private String boardName;
    private List<String> category;
    private String writerProfileUrl;
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
}
