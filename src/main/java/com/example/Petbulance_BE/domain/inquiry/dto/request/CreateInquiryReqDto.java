package com.example.Petbulance_BE.domain.inquiry.dto.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CreateInquiryReqDto {
    private String type;
    private String companyName;
    private String managerName;
    private String managerPosition;
    private String phone;
    private String email;
    private String interestType;
    private String content;
    private boolean privacyConsent;
}
