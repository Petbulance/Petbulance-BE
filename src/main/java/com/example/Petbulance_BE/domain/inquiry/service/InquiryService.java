package com.example.Petbulance_BE.domain.inquiry.service;

import com.example.Petbulance_BE.domain.inquiry.dto.request.CreateInquiryReqDto;
import com.example.Petbulance_BE.domain.inquiry.dto.response.CreateInquiryResDto;
import com.example.Petbulance_BE.domain.inquiry.entity.Inquiry;
import com.example.Petbulance_BE.domain.inquiry.repository.InquiryRepository;
import com.example.Petbulance_BE.domain.inquiry.type.InquiryType;
import com.example.Petbulance_BE.domain.inquiry.type.InterestType;
import com.example.Petbulance_BE.domain.user.entity.Users;
import com.example.Petbulance_BE.global.common.error.exception.CustomException;
import com.example.Petbulance_BE.global.common.error.exception.ErrorCode;
import com.example.Petbulance_BE.global.util.UserUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class InquiryService {
    private final InquiryRepository inquiryRepository;

    public CreateInquiryResDto createInquiry(CreateInquiryReqDto dto) {
        // enum타입 올바른지 확인
        InquiryType inquiryType = InquiryType.fromString(dto.getType());
        InterestType interestType = InterestType.fromString(dto.getInterestType());

        if(inquiryType == null || interestType == null) {
            throw new CustomException(ErrorCode.INVALID_TYPE);
        }

        // 연락처 한개 이상 입력되었는지
        if(dto.getEmail().isEmpty() && dto.getPhone().isEmpty()) {
            throw new CustomException(ErrorCode.INVALID_CONTACT_INFO);
        }

        // 동의했는지
        if(!dto.isPrivacyConsent()) {
            throw new CustomException(ErrorCode.PRIVACY_CONSENT_REQUIRED);
        }

        Users currentUser = UserUtil.getCurrentUser();
        inquiryRepository.save(
                Inquiry.builder()
                        .user(currentUser)
                        .type(inquiryType)
                        .companyName(dto.getCompanyName())
                        .managerName(dto.getManagerName())
                        .managerPosition(dto.getManagerPosition())
                        .phone(dto.getPhone())
                        .email(dto.getEmail())
                        .interestType(interestType)
                        .content(dto.getContent())
                        .privacyConsent(dto.isPrivacyConsent())
                        .build()
        );

        return new CreateInquiryResDto("광고/병원 제휴 문의가 정상적으로 접수되었습니다.");
    }
}
