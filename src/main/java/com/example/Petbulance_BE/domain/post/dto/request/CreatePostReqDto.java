package com.example.Petbulance_BE.domain.post.dto.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class CreatePostReqDto {
    private Long boardId;
    private String category;
    private String title;
    private String content;
    private List<String> imageUrls;
}
