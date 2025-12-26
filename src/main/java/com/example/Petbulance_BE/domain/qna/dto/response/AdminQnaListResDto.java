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
public class AdminQnaListResDto {
    private Long qnaId;
    private QnaStatus status;
    private String title;
    private String content;
    private String writerNickname;
    private String createdAt;

    public AdminQnaListResDto(Long qnaId, QnaStatus status, String title, String content, String writerNickname, LocalDateTime createdAt) {
        this.qnaId = qnaId;
        this.status = status;
        this.title = title;
        this.writerNickname = writerNickname;
        this.createdAt = TimeUtil.formatCreatedAt(createdAt);
    }
}
