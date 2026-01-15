package com.example.Petbulance_BE.domain.report.controller;

import com.example.Petbulance_BE.domain.report.dto.request.ReportCreateReqDto;
import com.example.Petbulance_BE.domain.report.dto.response.ReportCreateResDto;
import com.example.Petbulance_BE.domain.report.service.ReportService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/reports")
public class ReportController {
    private final ReportService reportService;

    @PostMapping
    public ReportCreateResDto createReport(@RequestBody @Valid ReportCreateReqDto reqDto) {
        return reportService.createReport(reqDto);
    }

}
