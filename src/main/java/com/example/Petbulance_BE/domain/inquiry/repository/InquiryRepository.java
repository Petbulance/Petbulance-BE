package com.example.Petbulance_BE.domain.inquiry.repository;

import com.example.Petbulance_BE.domain.inquiry.entity.Inquiry;
import org.springframework.data.jpa.repository.JpaRepository;

public interface InquiryRepository extends JpaRepository<Inquiry, Long>, InquiryRepositoryCustom {
}
