package com.example.Petbulance_BE.domain.qna.dto.response;

import com.example.Petbulance_BE.domain.qna.type.QnaStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class QnaListResDto {
    private Long qnaId;
    private String title;
    private QnaStatus content;
    private LocalDateTime createdAt;
    private String status;
}
