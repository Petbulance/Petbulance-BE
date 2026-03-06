package com.example.Petbulance_BE.domain.post.dto.request;

import com.example.Petbulance_BE.domain.post.type.Topic;
import com.example.Petbulance_BE.global.common.type.AnimalType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CreatePostReqDto {
    @NotNull(message = "동물종(type)은 필수입니다.")
    private AnimalType type;

    @NotNull(message = "주제(topic)는 필수입니다.")
    private Topic topic;

    @NotBlank(message = "게시글 제목(title)은 필수입니다.")
    private String title;

    @NotBlank(message = "게시글 내용(content)은 필수입니다.")
    private String content;

    private List<String> imageUrls;
}
