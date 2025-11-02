package com.example.Petbulance_BE.domain.qna.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class DeleteQnaResDto {
    private Long qnaId;
    private String message;
}
