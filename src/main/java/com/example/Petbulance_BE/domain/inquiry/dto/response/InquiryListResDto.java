package com.example.Petbulance_BE.domain.inquiry.dto.response;

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
    private String createdAt;

    public InquiryListResDto(Long id, String content, String type, String interestType, LocalDateTime createdAt) {
        this.id = id;
        this.content = content;
        this.type = type;
        this.interestType = interestType;
        this.createdAt = TimeUtil.formatCreatedAt(createdAt);
    }
}
