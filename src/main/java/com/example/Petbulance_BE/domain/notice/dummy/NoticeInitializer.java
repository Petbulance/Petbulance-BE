package com.example.Petbulance_BE.domain.notice.dummy;

import com.example.Petbulance_BE.domain.notice.entity.Notice;
import com.example.Petbulance_BE.domain.notice.entity.NoticeFile;
import com.example.Petbulance_BE.domain.notice.repository.NoticeFileRepository;
import com.example.Petbulance_BE.domain.notice.repository.NoticeRepository;
import com.example.Petbulance_BE.domain.notice.type.NoticeStatus;
import com.example.Petbulance_BE.domain.notice.type.PostStatus;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.Random;

@Slf4j
//@Component
@RequiredArgsConstructor
public class NoticeInitializer implements ApplicationRunner {

    private final NoticeRepository noticeRepository;
    private final NoticeFileRepository noticeFileRepository;

    private final Random random = new Random();

    @Override
    public void run(ApplicationArguments args) {

        if (noticeRepository.count() > 0) {
            log.info("⏸ Notice 데이터가 이미 존재 — 생성 스킵");
            return;
        }

        log.info("🔹 Notice / NoticeFile 더미 데이터 생성 시작");

        NoticeStatus[] noticeStatuses = NoticeStatus.values();
        PostStatus[] postStatuses = PostStatus.values();

        for (int i = 1; i <= 20; i++) {

            LocalDate start = LocalDate.now().minusDays(random.nextInt(10));
            LocalDate end = start.plusDays(random.nextInt(10) + 1);

            Notice notice = Notice.builder()
                    .noticeStatus(noticeStatuses[random.nextInt(noticeStatuses.length)])
                    .postStatus(postStatuses[random.nextInt(postStatuses.length)])
                    .title("공지사항 테스트 #" + i)
                    .content("테스트 공지 내용입니다. index = " + i)
                    .postStartDate(start)
                    .postEndDate(end)
                    .build();

            noticeRepository.save(notice);

            // 🔹 NoticeFile 0~5개 랜덤 생성
            int fileCount = random.nextInt(6); // 0~5

            for (int j = 1; j <= fileCount; j++) {
                NoticeFile file = NoticeFile.builder()
                        .notice(notice)
                        .fileName("test_file_" + i + "_" + j + ".png")
                        .fileUrl("https://dummy.s3.amazonaws.com/notice/" + i + "/file" + j)
                        .build();

                noticeFileRepository.save(file);
            }
        }

        log.info("✅ Notice 20개 + NoticeFile 랜덤 생성 완료");
    }
}
