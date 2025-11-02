package com.example.Petbulance_BE.domain.post.dto.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UpdatePostReqDto {
    private String category;
    private String title;
    private String content;
    private List<ImageUpdateDto> imagesToKeepOrAdd;
    private List<String> imageUrlsToDelete;

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public class ImageUpdateDto {
        private String imageUrl;
        private int imageOrder;
        private boolean thumbnail;
    }
}
