package com.example.Petbulance_BE.domain.report.repository;

import com.example.Petbulance_BE.domain.report.dto.response.PagingReportListResDto;

public interface ReportRepositoryCustom {
    PagingReportListResDto findPagingReports(int page, int size);
}
