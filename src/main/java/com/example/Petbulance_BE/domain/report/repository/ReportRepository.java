package com.example.Petbulance_BE.domain.report.repository;

import com.example.Petbulance_BE.domain.report.entity.Report;
import com.example.Petbulance_BE.domain.report.type.ReportType;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ReportRepository extends JpaRepository<Report, Long>, ReportRepositoryCustom {
    int countByProcessedFalse();   // 미처리 신고 수
}
