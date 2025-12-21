package com.example.Petbulance_BE.domain.inquiry.service;

import com.example.Petbulance_BE.domain.inquiry.controller.AnswerInquiryResDto;
import com.example.Petbulance_BE.domain.inquiry.dto.request.AnswerInquiryReqDto;
import com.example.Petbulance_BE.domain.inquiry.dto.request.CreateInquiryReqDto;
import com.example.Petbulance_BE.domain.inquiry.dto.request.UpdateInquiryReqDto;
import com.example.Petbulance_BE.domain.inquiry.dto.response.*;
import com.example.Petbulance_BE.domain.inquiry.entity.Inquiry;
import com.example.Petbulance_BE.domain.inquiry.repository.InquiryRepository;
import com.example.Petbulance_BE.domain.inquiry.type.InquiryType;
import com.example.Petbulance_BE.domain.inquiry.type.InterestType;
import com.example.Petbulance_BE.domain.notice.dto.response.PagingNoticeListResDto;
import com.example.Petbulance_BE.domain.user.entity.Users;
import com.example.Petbulance_BE.global.common.error.exception.CustomException;
import com.example.Petbulance_BE.global.common.error.exception.ErrorCode;
import com.example.Petbulance_BE.global.util.UserUtil;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class InquiryService {
    private final InquiryRepository inquiryRepository;

    @Transactional
    public CreateInquiryResDto createInquiry(CreateInquiryReqDto dto) {
        InquiryType inquiryType = InquiryType.fromString(dto.getType());
        InterestType interestType = InterestType.fromString(dto.getInterestType());

        // 연락처 한개 이상 입력되었는지
        if(dto.getEmail().isEmpty() && dto.getPhone().isEmpty()) {
            throw new CustomException(ErrorCode.INVALID_CONTACT_INFO);
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

    @Transactional
    public UpdateInquiryResDto updateInquiry(@Valid UpdateInquiryReqDto dto, Long inquiryId) {
        // enum타입 올바른지 확인
        InquiryType inquiryType = InquiryType.fromString(dto.getType());
        InterestType interestType = InterestType.fromString(dto.getInterestType());

        // 연락처 한개 이상 입력되었는지
        if(dto.getEmail().isEmpty() && dto.getPhone().isEmpty()) {
            throw new CustomException(ErrorCode.INVALID_CONTACT_INFO);
        }

        Inquiry inquiry = getInquiry(inquiryId);

        Users currentUser = UserUtil.getCurrentUser();
        if(currentUser != null) {
            verifyInquiryUser(inquiry, currentUser);
        }
        inquiry.update(dto, inquiryType, interestType);

        return new UpdateInquiryResDto("광고/제휴 문의가 정상적으로 수정되었습니다.");
    }

    private void verifyInquiryUser(Inquiry inquiry, Users currentUser) {
        if(!inquiry.getUser().getId().equals(currentUser.getId())) {
            throw new CustomException(ErrorCode.FORBIDDEN_INQUIRY_ACCESS);
        }
    }

    private Inquiry getInquiry(Long inquiryId) {
       return inquiryRepository.findById(inquiryId).orElseThrow(() ->
                new CustomException(ErrorCode.INQUIRY_NOT_FOUND));
    }

    @Transactional
    public DeleteInquiryResDto deleteInquiry(Long inquiryId) {
        Inquiry inquiry = getInquiry(inquiryId);
        Users currentUser = UserUtil.getCurrentUser();

        if(currentUser != null) {
            verifyInquiryUser(inquiry, currentUser);
        }

        inquiryRepository.delete(inquiry);

        return new DeleteInquiryResDto("광고/제휴 문의가 정상적으로 삭제되었습니다.");
    }

    public PagingInquiryListResDto inquiryList(Pageable pageable, Long lastInquiryId) {
        return inquiryRepository.findInquiryList(pageable, lastInquiryId, UserUtil.getCurrentUser());
    }

    public DetailInquiryResDto detailInquiry(Long inquiryId) {
        Inquiry inquiry = getInquiry(inquiryId);

        Users currentUser = UserUtil.getCurrentUser();
        if(currentUser != null) {
            verifyInquiryUser(inquiry, currentUser);
        }

        return DetailInquiryResDto.from(inquiry);
    }

    public PagingAdminInquiryListResDto adminInquiryList(Pageable pageable, Long lastInquiryId) {
        return inquiryRepository.findAdminInquiryList(pageable, lastInquiryId);
    }

    public AnswerInquiryResDto answerInquiry(Long inquiryId, AnswerInquiryReqDto reqDto) {
        Inquiry inquiry = getInquiry(inquiryId);
        inquiry.answer(reqDto.getContent());

        return new AnswerInquiryResDto("답변이 정상적으로 작성되었습니다.");
    }
}
