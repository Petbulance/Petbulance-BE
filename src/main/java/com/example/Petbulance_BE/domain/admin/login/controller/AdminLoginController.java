package com.example.Petbulance_BE.domain.admin.login.controller;

import com.example.Petbulance_BE.domain.admin.login.dto.AdminLoginReqDto;
import com.example.Petbulance_BE.domain.admin.login.dto.AdminRegisterReqDto;
import com.example.Petbulance_BE.domain.admin.login.service.AdminLoginService;
import com.example.Petbulance_BE.domain.adminlog.aop.AdminLoggable;
import com.example.Petbulance_BE.domain.adminlog.type.AdminActionType;
import com.example.Petbulance_BE.domain.adminlog.type.AdminPageType;
import com.example.Petbulance_BE.domain.adminlog.type.AdminTargetType;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;


@RestController
@RequiredArgsConstructor
@RequestMapping("/admin/login")
public class AdminLoginController {

    private final AdminLoginService adminLoginService;

    @AdminLoggable(
            pageType = AdminPageType.ACTION_LOG,
            actionType = AdminActionType.LOGIN,
            targetType = AdminTargetType.ACCOUNT,
            description = "로그인"
    )
    @PostMapping
    public Map<String, String> adminLogin(@RequestBody AdminLoginReqDto adminLoginReqDto) {
        System.out.println("서비스 진입 성공! 유저네임: " + adminLoginReqDto.getUsername());
        return adminLoginService.adminLoginProcess(adminLoginReqDto);

    }

}
