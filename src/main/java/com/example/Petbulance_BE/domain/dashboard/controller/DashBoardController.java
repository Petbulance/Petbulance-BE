package com.example.Petbulance_BE.domain.dashboard.controller;

import com.example.Petbulance_BE.domain.dashboard.dto.request.EventVisitReqDto;
import com.example.Petbulance_BE.domain.dashboard.dto.response.DashBoardSummaryResDto;
import com.example.Petbulance_BE.domain.dashboard.dto.response.EventVisitResDto;
import com.example.Petbulance_BE.domain.dashboard.service.DashBoardService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/admin/dashboard")
@RequiredArgsConstructor
public class DashBoardController {
    private final DashBoardService dashBoardService;

    @GetMapping
    public DashBoardSummaryResDto dashBoardSummary() {
        return dashBoardService.dashBoardSummary();
    }

    @PostMapping
    public EventVisitResDto eventVisit(@RequestBody EventVisitReqDto reqDto) {
        return dashBoardService.eventVisit(reqDto);
    }


}
