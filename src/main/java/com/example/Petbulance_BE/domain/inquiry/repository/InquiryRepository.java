package com.example.Petbulance_BE.domain.inquiry.repository;

import com.example.Petbulance_BE.domain.inquiry.entity.Inquiry;
import com.example.Petbulance_BE.domain.inquiry.type.InquiryAnswerType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.time.LocalDateTime;

public interface InquiryRepository extends JpaRepository<Inquiry, Long>, InquiryRepositoryCustom {
    // 필드명이 inquiryAnswerType이므로 이에 맞춰야 합니다.
    int countByInquiryAnswerType(InquiryAnswerType inquiryAnswerType);

    // 날짜 조회용
    long countByCreatedAtBetween(LocalDateTime start, LocalDateTime end);
}
