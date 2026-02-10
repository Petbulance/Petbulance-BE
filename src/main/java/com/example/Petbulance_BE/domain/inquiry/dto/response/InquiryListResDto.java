package com.example.Petbulance_BE.domain.inquiry.dto.response;

import com.example.Petbulance_BE.domain.inquiry.type.InquiryAnswerType;
import com.example.Petbulance_BE.global.util.TimeUtil;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class InquiryListResDto {
    private Long id;
    private String content;
    private String type;
    private String interestType;
    private InquiryAnswerType inquiryAnswerType;
    private String createdAt;

    public InquiryListResDto(Long id, String content, String type, String interestType, InquiryAnswerType inquiryAnswerType, LocalDateTime createdAt) {
        this.id = id;
        this.content = content;
        this.type = type;
        this.interestType = interestType;
        this.inquiryAnswerType = inquiryAnswerType;
        this.createdAt = TimeUtil.formatCreatedAt(createdAt);
    }
}
