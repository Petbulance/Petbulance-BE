package com.example.Petbulance_BE.domain.post.dto.response;

import com.example.Petbulance_BE.domain.post.entity.Post;
import com.example.Petbulance_BE.domain.post.entity.PostImage;
import com.example.Petbulance_BE.domain.post.type.Topic;
import com.example.Petbulance_BE.global.common.type.AnimalType;
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
    private AnimalType type;
    private Topic topic;
    private String title;
    private String content;
    private List<String> imageUrls;
    private LocalDateTime updatedAt;

    public static UpdatePostResDto from(Post post, List<PostImage> postImages) {
        return UpdatePostResDto.builder()
                .postId(post.getId())
                .type(post.getAnimalType())
                .topic(post.getTopic())
                .title(post.getTitle())
                .content(post.getContent())
                .imageUrls(
                        postImages.stream()
                                .sorted((a, b) -> Integer.compare(a.getImageOrder(), b.getImageOrder()))
                                .map(PostImage::getImageUrl)
                                .collect(Collectors.toList())
                )
                .updatedAt(post.getUpdatedAt())
                .build();
    }
}
