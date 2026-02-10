package com.example.Petbulance_BE.domain.post.dto.response;

import com.example.Petbulance_BE.domain.post.entity.Post;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class CreatePostResDto {
    private Long postId;
    private Long boardId;
    private String category;
    private String title;
    private String content;
    private List<String> imageUrls;

    public static CreatePostResDto of(Post post, Long boardId, List<String> imageUrls) {
        CreatePostResDto dto = new CreatePostResDto();
        dto.postId = post.getId();
        dto.boardId = boardId;
        dto.category = post.getCategory().toString();
        dto.title = post.getTitle();
        dto.content = post.getContent();
        dto.imageUrls = imageUrls;
        return dto;
    }
}
