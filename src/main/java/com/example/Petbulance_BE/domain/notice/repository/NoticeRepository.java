package com.example.Petbulance_BE.domain.notice.repository;

import com.example.Petbulance_BE.domain.notice.entity.Notice;
import org.springframework.data.jpa.repository.JpaRepository;

public interface NoticeRepository extends JpaRepository<Notice, Long>, NoticeRepositoryCustom {
}
