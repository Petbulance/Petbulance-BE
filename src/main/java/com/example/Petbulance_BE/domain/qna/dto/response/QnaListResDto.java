package com.example.Petbulance_BE.domain.qna.dto.response;

import com.example.Petbulance_BE.domain.qna.type.QnaStatus;
import com.example.Petbulance_BE.global.util.TimeUtil;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
public class QnaListResDto {
    private Long id;
    private String title;
    private String author;
    private LocalDateTime createdAt;
    private QnaStatus status;


    public QnaListResDto(Long id, String title, String author, LocalDateTime createdAt, QnaStatus status) {
        this.id = id;
        this.title = title;
        this.author = author;
        this.createdAt = createdAt;
        this.status = status;
    }
}
