package com.example.Petbulance_BE.domain.qna.dto.response;

import com.example.Petbulance_BE.domain.qna.entity.Qna;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UpdateQnaResDto {
    private Long qnaId;
    private String title;
    private String content;
    private LocalDateTime updatedAt;

    public static UpdateQnaResDto from(Qna qna) {
        UpdateQnaResDto dto = new UpdateQnaResDto();
        dto.qnaId = qna.getId();
        dto.title = qna.getTitle();
        dto.content = qna.getContent();
        dto.updatedAt = qna.getCreatedAt();
        return dto;
    }
}
