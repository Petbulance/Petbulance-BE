package com.example.Petbulance_BE.domain.inquiry.type;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public enum InquiryAnswerType {
    ANSWER_WAITING("답변 대기"),
    ANSWER_COMPLETED("답변 완료");

    private String description;
}
