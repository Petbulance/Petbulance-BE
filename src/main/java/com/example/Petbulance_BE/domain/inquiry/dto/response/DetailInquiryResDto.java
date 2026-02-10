package com.example.Petbulance_BE.domain.inquiry.dto.response;

import com.example.Petbulance_BE.domain.inquiry.entity.Inquiry;
import com.example.Petbulance_BE.domain.inquiry.type.InquiryAnswerType;
import com.example.Petbulance_BE.domain.qna.dto.response.DetailQnaResDto;
import com.example.Petbulance_BE.global.util.TimeUtil;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class DetailInquiryResDto {
    private Long inquiryId;
    private String type;
    private String companyName;
    private String managerName;
    private String managerPosition;
    private String phone;
    private String email;
    private String interestType;
    private String content;
    private String createdAt;
    private String updatedAt;
    private Answer answer;

    public static DetailInquiryResDto from(Inquiry inquiry) {
        DetailInquiryResDto resDto = new DetailInquiryResDto();
        resDto.inquiryId = inquiry.getId();
        resDto.type = inquiry.getType().toString();
        resDto.companyName = inquiry.getCompanyName();
        resDto.managerName = inquiry.getManagerName();
        resDto.managerPosition = inquiry.getManagerPosition();
        resDto.phone = inquiry.getPhone();
        resDto.email = inquiry.getEmail();
        resDto.interestType = inquiry.getInterestType().toString();
        resDto.content = inquiry.getContent();
        resDto.createdAt = TimeUtil.formatCreatedAt(inquiry.getCreatedAt());
        resDto.updatedAt = TimeUtil.formatCreatedAt(inquiry.getUpdatedAt());
        resDto.answer = Answer.from(inquiry.getAnswerContent(), inquiry.getAnsweredAt(), inquiry.getInquiryAnswerType());
        return resDto;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    private static class Answer {
        private String content;
        private String answeredAt;
        private InquiryAnswerType inquiryAnswerType;

        public static Answer from(String answerContent, LocalDateTime answeredAt, InquiryAnswerType inquiryAnswerType) {
            Answer a = new Answer();
            a.content = answerContent;
            a.answeredAt = TimeUtil.formatCreatedAt(answeredAt);
            a.inquiryAnswerType = inquiryAnswerType;
            return a;
        }
    }
}
