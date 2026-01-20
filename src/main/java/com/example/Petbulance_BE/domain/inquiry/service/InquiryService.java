package com.example.Petbulance_BE.domain.inquiry.service;

import com.example.Petbulance_BE.domain.adminlog.entity.AdminActionLog;
import com.example.Petbulance_BE.domain.adminlog.repository.AdminActionLogRepository;
import com.example.Petbulance_BE.domain.adminlog.type.*;
import com.example.Petbulance_BE.domain.inquiry.dto.response.AnswerInquiryResDto;
import com.example.Petbulance_BE.domain.inquiry.dto.request.AnswerInquiryReqDto;
import com.example.Petbulance_BE.domain.inquiry.dto.request.CreateInquiryReqDto;
import com.example.Petbulance_BE.domain.inquiry.dto.request.UpdateInquiryReqDto;
import com.example.Petbulance_BE.domain.inquiry.dto.response.*;
import com.example.Petbulance_BE.domain.inquiry.entity.Inquiry;
import com.example.Petbulance_BE.domain.inquiry.repository.InquiryRepository;
import com.example.Petbulance_BE.domain.inquiry.type.InquiryAnswerType;
import com.example.Petbulance_BE.domain.inquiry.type.InquiryType;
import com.example.Petbulance_BE.domain.inquiry.type.InterestType;
import com.example.Petbulance_BE.domain.user.entity.Users;
import com.example.Petbulance_BE.global.common.error.exception.CustomException;
import com.example.Petbulance_BE.global.common.error.exception.ErrorCode;
import com.example.Petbulance_BE.global.util.UserUtil;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

@Service
@RequiredArgsConstructor
public class InquiryService {
    private final InquiryRepository inquiryRepository;
    private final AdminActionLogRepository adminActionLogRepository;

    @Transactional
    public CreateInquiryResDto createInquiry(CreateInquiryReqDto dto) {
        // 연락처 한개 이상 입력되었는지
        if(dto.getEmail().isEmpty() && dto.getPhone().isEmpty()) {
            throw new CustomException(ErrorCode.INVALID_CONTACT_INFO);
        }

        Users currentUser = UserUtil.getCurrentUser();
        inquiryRepository.save(
                Inquiry.builder()
                        .user(currentUser)
                        .type(dto.getType())
                        .companyName(dto.getCompanyName())
                        .managerName(dto.getManagerName())
                        .managerPosition(dto.getManagerPosition())
                        .phone(dto.getPhone())
                        .email(dto.getEmail())
                        .interestType(dto.getInterestType())
                        .content(dto.getContent())
                        .privacyConsent(dto.isPrivacyConsent())
                        .build()
        );

        return new CreateInquiryResDto("광고/병원 제휴 문의가 정상적으로 접수되었습니다.");
    }

    @Transactional
    public UpdateInquiryResDto updateInquiry(@Valid UpdateInquiryReqDto dto, Long inquiryId) {
        // 연락처 한개 이상 입력되었는지
        if(dto.getEmail().isEmpty() && dto.getPhone().isEmpty()) {
            throw new CustomException(ErrorCode.INVALID_CONTACT_INFO);
        }

        Inquiry inquiry = getInquiry(inquiryId);

        Users currentUser = UserUtil.getCurrentUser();
        if(currentUser != null) {
            verifyInquiryUser(inquiry, currentUser);
        }
        inquiry.update(dto, dto.getType(), dto.getInterestType());

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

    @Transactional(readOnly = true)
    public DetailInquiryResDto detailInquiry(Long inquiryId) {
        Inquiry inquiry = getInquiry(inquiryId);

        Users currentUser = UserUtil.getCurrentUser();
        if(currentUser != null) {
            verifyInquiryUser(inquiry, currentUser);
        }

        adminActionLogRepository.save(AdminActionLog.builder()
                .actorType(AdminActorType.ADMIN)
                .admin(currentUser)
                .pageType(AdminPageType.CUSTOMER_CENTER)
                .actionType(AdminActionType.READ)
                .targetType(AdminTargetType.CS_DETAIL)
                .resultType(AdminActionResult.SUCCESS)
                .description(String.format("[조회] %d번 제휴 문의 상세 열람 (작성자: %s)", inquiryId, inquiry.getUser().getNickname()))
                .build()
        );

        return DetailInquiryResDto.from(inquiry);
    }


    @Transactional
    public AnswerInquiryResDto answerInquiry(Long inquiryId, AnswerInquiryReqDto reqDto) {
        Inquiry inquiry = getInquiry(inquiryId);

        if(inquiry.getInquiryAnswerType() == InquiryAnswerType.ANSWER_WAITING) {
            inquiry.answer(reqDto.getContent());
        } else {
            throw new CustomException(ErrorCode.ALREADY_WRITTEN_ANSWER);
        }

        Users currentUser = UserUtil.getCurrentUser();
        adminActionLogRepository.save(AdminActionLog.builder()
                .actorType(AdminActorType.ADMIN)
                .admin(currentUser)
                .pageType(AdminPageType.CUSTOMER_CENTER)
                .actionType(AdminActionType.UPDATE)
                .targetType(AdminTargetType.CS_ANSWER)
                .resultType(AdminActionResult.SUCCESS)
                .description(String.format("[작성] %d번 제휴 문의 답변 발송 및 상태 변경 (대기 -> 처리)", inquiryId))
                .build()
        );


        return new AnswerInquiryResDto("답변이 정상적으로 작성되었습니다.");
    }

    public PagingAdminInquiryListResDto adminInquiryList(int page, int size) {

        adminActionLogRepository.save(AdminActionLog.builder()
                .actorType(AdminActorType.ADMIN)
                .admin(UserUtil.getCurrentUser())
                .pageType(AdminPageType.CUSTOMER_CENTER)
                .actionType(AdminActionType.READ)
                .targetType(AdminTargetType.CS_LIST)
                .resultType(AdminActionResult.SUCCESS)
                .description("[조회] 제휴 문의 리스트 진입")
                .build()
        );
        return inquiryRepository.findAdminInquiryList(page, size);
    }
}
