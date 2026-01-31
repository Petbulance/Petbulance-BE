package com.example.Petbulance_BE.domain.report.repository;

import com.example.Petbulance_BE.domain.report.dto.response.PagingReportListResDto;
import com.example.Petbulance_BE.domain.report.type.ReportType;

public interface ReportRepositoryCustom {
    PagingReportListResDto findPagingReports(int page, int size);
}
