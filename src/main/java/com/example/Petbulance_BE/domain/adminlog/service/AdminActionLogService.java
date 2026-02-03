package com.example.Petbulance_BE.domain.adminlog.service;

import com.example.Petbulance_BE.domain.adminlog.dto.response.PagingAdminActionLogListResDto;
import com.example.Petbulance_BE.domain.adminlog.entity.AdminActionLog;
import com.example.Petbulance_BE.domain.adminlog.repository.AdminActionLogRepository;
import com.example.Petbulance_BE.domain.adminlog.type.*;
import com.example.Petbulance_BE.domain.notice.type.PostStatus;
import com.example.Petbulance_BE.domain.user.entity.Users;
import com.example.Petbulance_BE.global.util.UserUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AdminActionLogService {
    private final AdminActionLogRepository adminActionLogRepository;

    @Transactional(readOnly = true)
    public PagingAdminActionLogListResDto adminActionLogList(int page, int size) {

        Users currentUser = UserUtil.getCurrentUser();
        return adminActionLogRepository.adminActionLogList(page, size);
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void save(AdminActionLog adminActionLog) {
        adminActionLogRepository.save(adminActionLog);
    }

}
