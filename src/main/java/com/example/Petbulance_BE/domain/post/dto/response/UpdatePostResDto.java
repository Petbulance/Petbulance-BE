package com.example.Petbulance_BE.domain.post.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UpdatePostResDto {
    private Long postId;
    private Long boardId;
    private String category;
    private String title;
    private String content;
    private List<String> imageUrls;
    private LocalDateTime updatedAt;
}
