package com.example.Petbulance_BE.domain.post.dto.request;

import com.example.Petbulance_BE.domain.post.type.Category;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UpdatePostReqDto {
    @NotBlank(message = "카테고리(category)는 필수입니다.")
    private Category category;

    @NotBlank(message = "게시글 제목(title)은 필수입니다.")
    private String title;

    @NotBlank(message = "게시글 내용(content)은 필수입니다.")
    private String content;
    private List<ImageUpdateDto> imagesToKeepOrAdd;
    private List<String> imageUrlsToDelete;

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class ImageUpdateDto {
        private String imageUrl;
        private int imageOrder;
        private boolean thumbnail;
    }
}
