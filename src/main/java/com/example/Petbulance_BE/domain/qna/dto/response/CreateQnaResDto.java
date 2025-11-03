package com.example.Petbulance_BE.domain.qna.dto.response;

import com.example.Petbulance_BE.domain.qna.entity.Qna;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CreateQnaResDto {
    private Long qnaId;
    private String title;
    private String content;
    private LocalDateTime createdAt;

    public static CreateQnaResDto from(Qna qna) {
        CreateQnaResDto dto = new CreateQnaResDto();
        dto.qnaId = qna.getId();
        dto.title = qna.getTitle();
        dto.content = qna.getContent();
        dto.createdAt = qna.getCreatedAt();
        return dto;
    }
}
