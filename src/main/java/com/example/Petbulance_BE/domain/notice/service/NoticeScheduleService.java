package com.example.Petbulance_BE.domain.notice.service;

import com.example.Petbulance_BE.domain.notice.repository.NoticeRepository;
import com.example.Petbulance_BE.domain.notice.type.PostStatus;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;

@Slf4j
@Service
@RequiredArgsConstructor
public class NoticeScheduleService {

    private final NoticeRepository noticeRepository;

    @Transactional
    public void deactivateExpiredNotices() {

        LocalDate today = LocalDate.now();

        int updated = noticeRepository.deactivateExpired(
                today,
                PostStatus.INACTIVE
        );

        log.info("🔔 만료된 공지 {}건 INACTIVE 처리 완료", updated);
    }
}
