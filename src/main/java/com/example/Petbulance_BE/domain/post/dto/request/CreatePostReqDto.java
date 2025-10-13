package com.example.Petbulance_BE.domain.post.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class CreatePostReqDto {
    @NotNull(message = "게시판 ID(boardId)는 필수입니다.")
    private Long boardId;

    @NotBlank(message = "카테고리(category)는 필수입니다.")
    private String category;

    @NotBlank(message = "게시글 제목(title)은 필수입니다.")
    private String title;

    @NotBlank(message = "게시글 내용(content)은 필수입니다.")
    private String content;

    private List<String> imageUrls;
}
