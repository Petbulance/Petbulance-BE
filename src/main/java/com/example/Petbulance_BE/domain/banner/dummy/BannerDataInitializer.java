package com.example.Petbulance_BE.domain.banner.dummy;


import com.example.Petbulance_BE.domain.banner.entity.Banner;
import com.example.Petbulance_BE.domain.banner.repository.BannerRepository;
import com.example.Petbulance_BE.domain.notice.entity.Notice;
import com.example.Petbulance_BE.domain.notice.repository.NoticeRepository;
import com.example.Petbulance_BE.domain.notice.type.NoticeStatus;
import com.example.Petbulance_BE.domain.notice.type.PostStatus;
import com.example.Petbulance_BE.domain.user.entity.Users;
import com.example.Petbulance_BE.domain.user.repository.UsersJpaRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;
import org.springframework.boot.CommandLineRunner;

import java.time.LocalDate;
import java.util.List;

@Slf4j
@Component
@Profile("local")
@RequiredArgsConstructor
public class BannerDataInitializer implements CommandLineRunner {

    private final BannerRepository bannerRepository;
    private final NoticeRepository noticeRepository;
    private final UsersJpaRepository userRepository;

    @Override
    public void run(String... args) {

        // 이미 데이터 있으면 생성 안 함 (중복 방지)
        if (bannerRepository.count() > 0) {
            log.info("[BannerInitializer] Banner already exists. Skip.");
            return;
        }

        Users user = userRepository.findAll()
                .stream()
                .findFirst()
                .orElseThrow(() -> new IllegalStateException("User not found"));

        List<Notice> notices = noticeRepository.findAll();
        if (notices.isEmpty()) {
            throw new IllegalStateException("Notice not found");
        }

        // ACTIVE 5개
        for (int i = 1; i <= 5; i++) {
            bannerRepository.save(
                    Banner.builder()
                            .notice(notices.get((i - 1) % notices.size()))
                            .users(user)
                            .noticeStatus(NoticeStatus.NOTICE)
                            .postStatus(PostStatus.ACTIVE)
                            .title("ACTIVE 배너 " + i)
                            .startDate(LocalDate.now().minusDays(1))
                            .endDate(LocalDate.now().plusDays(30))
                            .fileUrl("banner_active_" + i + ".png")
                            .build()
            );
        }

        // INACTIVE 5개
        for (int i = 1; i <= 5; i++) {
            bannerRepository.save(
                    Banner.builder()
                            .notice(notices.get((i - 1) % notices.size()))
                            .users(user)
                            .noticeStatus(NoticeStatus.ADVERTISING)
                            .postStatus(PostStatus.INACTIVE)
                            .title("INACTIVE 배너 " + i)
                            .startDate(LocalDate.now().minusDays(60))
                            .endDate(LocalDate.now().minusDays(30))
                            .fileUrl("banner_inactive_" + i + ".png")
                            .build()
            );
        }

        log.info("[BannerInitializer] Dummy banner data created (10 items)");
    }
}
