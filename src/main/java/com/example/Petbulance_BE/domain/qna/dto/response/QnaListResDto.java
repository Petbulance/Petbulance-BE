package com.example.Petbulance_BE.domain.qna.dto.response;

import com.example.Petbulance_BE.domain.qna.type.QnaStatus;
import com.example.Petbulance_BE.global.util.TimeUtil;
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
    private String createdAt;
    private String status;

    public QnaListResDto(Long qnaId, String title, QnaStatus content, LocalDateTime createdAt, String status) {
        this.qnaId = qnaId;
        this.title = title;
        this.content = content;
        this.createdAt = TimeUtil.formatCreatedAt(createdAt);
        this.status = status;
    }
}
