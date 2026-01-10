package com.example.Petbulance_BE.domain.adminlog.controller;

import com.example.Petbulance_BE.domain.adminlog.dto.response.PagingAdminActionLogListResDto;
import com.example.Petbulance_BE.domain.adminlog.service.AdminActionLogService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequestMapping("/admin/logs")
@RequiredArgsConstructor
public class AdminActionLogController {
    private final AdminActionLogService adminActionLogService;

    @GetMapping
    public PagingAdminActionLogListResDto adminActionLogList(@RequestParam(defaultValue = "1") int page,
                                                          @RequestParam(defaultValue = "20") int size) {
        return adminActionLogService.adminActionLogList(page, size);
    }
}
