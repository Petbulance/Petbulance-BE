package com.example.Petbulance_BE.domain.report.repository;

import com.example.Petbulance_BE.domain.report.entity.Report;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ReportRepository extends JpaRepository<Report, Long> {
}
