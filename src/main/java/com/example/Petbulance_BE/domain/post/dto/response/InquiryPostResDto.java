package com.example.Petbulance_BE.domain.post.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class InquiryPostResDto {

    private BoardInfo board;
    private PostInfo post;

    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class BoardInfo {
        private Long boardId;
        private String boardName;
        private String category;
    }

    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class PostInfo {
        private Long postId;
        private String title;
        private String writerNickname;
        private String writerProfileUrl;
        private String createdAt;
        private String content;
        private List<String> imageUrls;
        private Integer likeCount;
        private Integer commentCount;
        private Integer viewCount;
        private Boolean likedByUser;
        private Boolean isCurrentUserPost;
    }
}
