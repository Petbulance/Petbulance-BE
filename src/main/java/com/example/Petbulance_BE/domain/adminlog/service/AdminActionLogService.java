package com.example.Petbulance_BE.domain.adminlog.service;

import com.example.Petbulance_BE.domain.adminlog.dto.response.PagingAdminActionLogListResDto;
import com.example.Petbulance_BE.domain.adminlog.repository.AdminActionLogRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AdminActionLogService {
    private final AdminActionLogRepository adminActionLogRepository;

    @Transactional
    public PagingAdminActionLogListResDto adminActionLogList(int page, int size) {
        return adminActionLogRepository.adminActionLogList(page, size);
    }

}
