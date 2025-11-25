package com.example.Petbulance_BE.domain.inquiry.repository;

import com.example.Petbulance_BE.domain.inquiry.dto.response.PagingInquiryListResDto;
import com.example.Petbulance_BE.domain.user.entity.Users;
import org.springframework.data.domain.Pageable;

public interface InquiryRepositoryCustom {
    PagingInquiryListResDto findInquiryList(Pageable pageable, Long lastInquiryId, Users currentUser);
}

