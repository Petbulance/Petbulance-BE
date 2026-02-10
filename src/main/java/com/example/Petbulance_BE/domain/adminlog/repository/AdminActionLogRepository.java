package com.example.Petbulance_BE.domain.adminlog.repository;

import com.example.Petbulance_BE.domain.adminlog.entity.AdminActionLog;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AdminActionLogRepository extends JpaRepository<AdminActionLog, Long>, AdminActionLogRepositoryCustom {
}
