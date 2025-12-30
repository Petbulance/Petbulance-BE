package com.example.Petbulance_BE.domain.inquiry.repository;

import com.example.Petbulance_BE.domain.inquiry.dto.response.AdminInquiryListResDto;
import com.example.Petbulance_BE.domain.inquiry.dto.response.InquiryListResDto;
import com.example.Petbulance_BE.domain.inquiry.dto.response.PagingAdminInquiryListResDto;
import com.example.Petbulance_BE.domain.inquiry.dto.response.PagingInquiryListResDto;
import com.example.Petbulance_BE.domain.inquiry.entity.QInquiry;
import com.example.Petbulance_BE.domain.qna.dto.response.AdminQnaListResDto;
import com.example.Petbulance_BE.domain.qna.dto.response.PagingAdminQnaListResDto;
import com.example.Petbulance_BE.domain.qna.entity.QQna;
import com.example.Petbulance_BE.domain.user.entity.QUsers;
import com.example.Petbulance_BE.domain.user.entity.Users;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.BooleanExpression;
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
    public PagingAdminInquiryListResDto findAdminInquiryList(int page, int size) {
        QInquiry inquiry = QInquiry.inquiry;

        long offset = (long) (page - 1) * size;

        // 1) id 목록 가져오기
        List<Long> ids = queryFactory
                .select(inquiry.id)
                .from(inquiry)
                .orderBy(inquiry.createdAt.desc(), inquiry.id.desc())
                .offset(offset)
                .limit(size)
                .fetch();

        if (ids.isEmpty()) {
            return new PagingAdminInquiryListResDto(
                    List.of(), page, size, 0, 0, false, page > 1
            );
        }

        //  2) 실제 데이터 조회
        List<AdminInquiryListResDto> content = queryFactory
                .select(
                        Projections.constructor(
                                AdminInquiryListResDto.class,
                                inquiry.id,
                                inquiry.inquiryAnswerType,
                                inquiry.companyName,
                                inquiry.managerName,
                                inquiry.content,
                                inquiry.createdAt
                        )
                )
                .from(inquiry)
                .where(inquiry.id.in(ids))
                .orderBy(inquiry.createdAt.desc(), inquiry.id.desc())
                .fetch();

        // 🔹 3) 전체 개수
        long totalElements = queryFactory
                .select(inquiry.id.count())
                .from(inquiry)
                .fetchOne();

        int totalPages = (int) Math.ceil((double) totalElements / size);

        boolean hasNext = page < totalPages;
        boolean hasPrev = page > 1;

        return new PagingAdminInquiryListResDto(
                content,
                page,
                size,
                totalPages,
                totalElements,
                hasNext,
                hasPrev
        );
    }


}
