package com.example.Petbulance_BE.domain.report.controller;

import com.example.Petbulance_BE.domain.report.dto.response.PagingReportListResDto;
import com.example.Petbulance_BE.domain.report.dto.request.ReportActionReqDto;
import com.example.Petbulance_BE.domain.report.dto.response.ReportActionResDto;
import com.example.Petbulance_BE.domain.report.service.ReportService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/admin/reports")
public class AdminReportController {
    private final ReportService reportService;

    @GetMapping
    public PagingReportListResDto reportList(@RequestParam(required = false) Long lastReportId,
                                             @RequestParam(defaultValue = "10") Integer pageSize) {
        return reportService.reportList(lastReportId, pageSize);
    }

    @PatchMapping()
    public ReportActionResDto processReport(@PathVariable Long reportId,
                                            @RequestBody ReportActionReqDto reqDto) {
        return reportService.processReport(reportId, reqDto);
    }
}
