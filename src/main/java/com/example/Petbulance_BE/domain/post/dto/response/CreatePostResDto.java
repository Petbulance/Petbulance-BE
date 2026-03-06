package com.example.Petbulance_BE.domain.post.dto.response;

import com.example.Petbulance_BE.domain.post.entity.Post;
import com.example.Petbulance_BE.domain.post.type.Topic;
import com.example.Petbulance_BE.global.common.type.AnimalType;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class CreatePostResDto {
    private Long postId;
    private AnimalType type;
    private Topic topic;
    private String title;
    private String content;
    private List<String> imageUrls;

    public static CreatePostResDto of(Post savedPost, List<String> imageUrls) {
        CreatePostResDto dto = new CreatePostResDto();
        dto.postId = savedPost.getId();
        dto.type = savedPost.getAnimalType();
        dto.topic = savedPost.getTopic();
        dto.title = savedPost.getTitle();
        dto.content = savedPost.getContent();
        dto.imageUrls = imageUrls;
        return dto;
    };
}
