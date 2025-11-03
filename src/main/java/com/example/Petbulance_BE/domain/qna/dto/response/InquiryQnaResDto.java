package com.example.Petbulance_BE.domain.qna.dto.response;

import com.example.Petbulance_BE.domain.qna.entity.Qna;
import com.example.Petbulance_BE.domain.qna.type.QnaStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class InquiryQnaResDto {
    private Long qnaId;
    private String title;
    private String content;
    private LocalDateTime createdAt;
    private QnaStatus status;
    private Answer answer;

    public static InquiryQnaResDto from(Qna qna) {
        InquiryQnaResDto dto = new InquiryQnaResDto();
        dto.qnaId = qna.getId();
        dto.title = qna.getTitle();
        dto.content = qna.getContent();
        dto.createdAt = qna.getCreatedAt();
        dto.status = qna.getStatus();
        dto.answer = Answer.from(qna.getAnswerContent(), qna.getAnsweredAt());
        return dto;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    private static class Answer {
        private String content;
        private LocalDateTime answeredAt;


        public static Answer from(String answerContent, LocalDateTime answeredAt) {
            Answer a = new Answer();
            a.content = answerContent;
            a.answeredAt = answeredAt;
            return a;
        }
    }
}
