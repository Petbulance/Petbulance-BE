package com.example.Petbulance_BE.domain.notice.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class NoticeScheduler {

    private final NoticeScheduleService noticeScheduleService;

    @Scheduled(cron = "0 0 0 * * *")
    public void run() {
        log.info("Notice 스케줄러 실행 시작");
        noticeScheduleService.deactivateExpiredNotices();
    }
}