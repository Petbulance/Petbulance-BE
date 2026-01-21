package com.example.Petbulance_BE.domain.adminlog.repository;

import com.example.Petbulance_BE.domain.adminlog.dto.response.PagingAdminActionLogListResDto;

public interface AdminActionLogRepositoryCustom {
    PagingAdminActionLogListResDto adminActionLogList(int page, int size);
}

