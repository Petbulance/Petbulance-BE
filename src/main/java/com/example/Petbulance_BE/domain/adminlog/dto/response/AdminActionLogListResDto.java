package com.example.Petbulance_BE.domain.adminlog.dto.response;

import com.example.Petbulance_BE.domain.adminlog.type.AdminActionResult;
import com.example.Petbulance_BE.domain.adminlog.type.AdminPageType;
import com.example.Petbulance_BE.global.util.TimeUtil;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AdminActionLogListResDto {
    private Long actionLogId;
    private String adminName; // 관리자명
    private String pageType; // 페이지 위치
    private String description; // 행동 상세
    private AdminActionResult actionResult;
    private LocalDateTime createdAt;
}
