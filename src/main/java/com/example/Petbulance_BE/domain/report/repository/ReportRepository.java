package com.example.Petbulance_BE.domain.report.repository;

import com.example.Petbulance_BE.domain.report.entity.Report;
import com.example.Petbulance_BE.domain.report.type.ReportType;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

public interface ReportRepository extends JpaRepository<Report, Long>, ReportRepositoryCustom {
    // 커뮤니티(POST, COMMENT)용
    int countByReportTypeInAndCreatedAtBetween(List<ReportType> types, LocalDateTime start, LocalDateTime end);
    int countByReportTypeInAndProcessedFalse(List<ReportType> types);

    // 리뷰(REVIEW)용
    int countByReportTypeAndCreatedAtBetween(ReportType type, LocalDateTime start, LocalDateTime end);
    int countByReportTypeAndProcessedFalse(ReportType type);
}
