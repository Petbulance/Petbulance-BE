package com.example.Petbulance_BE.domain.post.dto.response;

import com.example.Petbulance_BE.domain.post.entity.Post;
import com.example.Petbulance_BE.domain.post.entity.PostImage;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UpdatePostResDto {
    private Long postId;
    private Long boardId;
    private String category;
    private String title;
    private String content;
    private List<String> imageUrls;
    private LocalDateTime updatedAt;

    public static UpdatePostResDto from(Post post, List<PostImage> postImages) {
        return UpdatePostResDto.builder()
                .postId(post.getId())
                .boardId(post.getBoard().getId())
                .category(post.getCategory().name())
                .title(post.getTitle())
                .content(post.getContent())
                .imageUrls(
                        postImages.stream()
                                .sorted((a, b) -> Integer.compare(a.getImageOrder(), b.getImageOrder()))
                                .map(PostImage::getImageUrl)
                                .collect(Collectors.toList())
                )
                .updatedAt(post.getUpdatedAt()) // Post 엔티티에 updatedAt 필드가 있어야 함
                .build();
    }
}
