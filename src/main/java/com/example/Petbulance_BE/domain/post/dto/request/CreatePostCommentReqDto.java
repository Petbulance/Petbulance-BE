package com.example.Petbulance_BE.domain.post.dto.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class CreatePostCommentReqDto {
    private String content;
    private Long parentId;
    private String mentionUserNickname;
    private String imageUrl;
    private boolean isSecret;
}
