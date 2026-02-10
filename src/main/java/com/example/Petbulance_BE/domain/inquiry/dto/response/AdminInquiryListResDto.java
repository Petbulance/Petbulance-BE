package com.example.Petbulance_BE.domain.inquiry.dto.response;

import com.example.Petbulance_BE.domain.inquiry.type.InquiryAnswerType;
import com.example.Petbulance_BE.global.util.TimeUtil;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AdminInquiryListResDto {
    private Long inquiryId;
    private InquiryAnswerType inquiryAnswerType;
    private String companyName;
    private String managerName;
    private String content;
    private String createdAt;

    public AdminInquiryListResDto(Long inquiryId, InquiryAnswerType inquiryAnswerType, String companyName, String managerName, String content, LocalDateTime createdAt) {
        this.inquiryId = inquiryId;
        this.inquiryAnswerType = inquiryAnswerType;
        this.companyName = companyName;
        this.managerName = managerName;
        this.content = content;
        this.createdAt = TimeUtil.formatCreatedAt(createdAt);
    }
}
