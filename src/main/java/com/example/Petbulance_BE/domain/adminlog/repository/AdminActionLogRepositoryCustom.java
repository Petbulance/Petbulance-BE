package com.example.Petbulance_BE.domain.adminlog.repository;

import com.example.Petbulance_BE.domain.adminlog.dto.response.PagingAdminActionLogListResDto;
import com.example.Petbulance_BE.domain.adminlog.type.AdminActionResult;
import com.example.Petbulance_BE.domain.adminlog.type.AdminPageType;

public interface AdminActionLogRepositoryCustom {
    PagingAdminActionLogListResDto adminActionLogList(String name, AdminPageType pageType, AdminActionResult resultType, int page, int size);
}

