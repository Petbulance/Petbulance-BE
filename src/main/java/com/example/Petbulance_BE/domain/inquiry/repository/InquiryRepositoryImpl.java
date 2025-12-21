package com.example.Petbulance_BE.domain.inquiry.repository;

import com.example.Petbulance_BE.domain.inquiry.dto.response.AdminInquiryListResDto;
import com.example.Petbulance_BE.domain.inquiry.dto.response.InquiryListResDto;
import com.example.Petbulance_BE.domain.inquiry.dto.response.PagingAdminInquiryListResDto;
import com.example.Petbulance_BE.domain.inquiry.dto.response.PagingInquiryListResDto;
import com.example.Petbulance_BE.domain.inquiry.entity.QInquiry;
import com.example.Petbulance_BE.domain.user.entity.Users;
import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class InquiryRepositoryImpl implements InquiryRepositoryCustom{
    private final JPAQueryFactory queryFactory;

    @Override
    public PagingInquiryListResDto findInquiryList(Pageable pageable, Long lastInquiryId, Users currentUser) {
        QInquiry inquiry = QInquiry.inquiry;

        int limit = pageable.getPageSize() + 1;

        List<InquiryListResDto> results = queryFactory
                .select(Projections.constructor(
                        InquiryListResDto.class,
                        inquiry.id,
                        inquiry.content,
                        inquiry.type.stringValue(),
                        inquiry.interestType.stringValue(),
                        inquiry.inquiryAnswerType,
                        inquiry.createdAt
                ))
                .from(inquiry)
                .where(inquiry.user.eq(currentUser)
                        .and(lastInquiryId != null ? inquiry.id.lt(lastInquiryId) : null))
                .orderBy(inquiry.id.desc(), inquiry.createdAt.desc())
                .limit(limit)
                .fetch();

        boolean hasNext = false;
        if (results.size() > pageable.getPageSize()) {
            hasNext = true;
            results = results.subList(0, pageable.getPageSize());
        }
        return new PagingInquiryListResDto(results, hasNext);
    }

    @Override
    public PagingAdminInquiryListResDto findAdminInquiryList(Pageable pageable, Long lastInquiryId) {

        QInquiry inquiry = QInquiry.inquiry;
        int limit = pageable.getPageSize() + 1;

        List<AdminInquiryListResDto> results = queryFactory
                .select(Projections.constructor(
                        AdminInquiryListResDto.class,
                        inquiry.id,
                        inquiry.inquiryAnswerType,
                        inquiry.companyName,
                        inquiry.managerName,
                        inquiry.content,
                        inquiry.createdAt
                ))
                .from(inquiry)
                .where(lastInquiryId != null ? inquiry.id.lt(lastInquiryId) : null)
                .orderBy(inquiry.id.desc())
                .limit(limit)
                .fetch();

        boolean hasNext = false;
        if (results.size() > pageable.getPageSize()) {
            hasNext = true;
            results = results.subList(0, pageable.getPageSize());
        }

        return new PagingAdminInquiryListResDto(results, hasNext);
    }

}
