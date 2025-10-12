package com.example.Petbulance_BE.domain.post.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Builder
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class PostLikeDto {
    private Long postId;
    private Long likeCount;
    private boolean liked;
}
