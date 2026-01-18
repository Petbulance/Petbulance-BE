package com.example.Petbulance_BE.domain.adminlog.dummy;

import com.example.Petbulance_BE.domain.adminlog.entity.AdminActionLog;
import com.example.Petbulance_BE.domain.adminlog.repository.AdminActionLogRepository;
import com.example.Petbulance_BE.domain.adminlog.type.*;
import com.example.Petbulance_BE.domain.user.entity.Users;
import com.example.Petbulance_BE.domain.user.repository.UsersJpaRepository;
import com.example.Petbulance_BE.global.common.type.Gender;
import com.example.Petbulance_BE.global.common.type.Role;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Random;

@Component
@RequiredArgsConstructor
public class AdminActionLogInitializer implements ApplicationRunner {

    private final AdminActionLogRepository adminActionLogRepository;
    private final UsersJpaRepository userRepository;

    private final Random random = new Random();

    @Override
    public void run(ApplicationArguments args) {

        if (adminActionLogRepository.count() > 0) return;

        /* ================= 관리자 유저 조회 or 생성 ================= */

        List<Users> admins = userRepository.findByRole(Role.ROLE_ADMIN);

        if (admins.isEmpty()) {
            Users admin1 = userRepository.save(createAdmin("관리자A", "01090000001"));
            Users admin2 = userRepository.save(createAdmin("관리자B", "01090000002"));
            admins = List.of(admin1, admin2);
        }

        /* ================= 관리자 행동 로그 50건 ================= */

        for (int i = 0; i < 50; i++) {

            boolean systemLog = i % 8 == 0;

            AdminActionLog log = AdminActionLog.builder()
                    .actorType(systemLog ? AdminActorType.SYSTEM : AdminActorType.ADMIN)
                    .admin(systemLog ? null : admins.get(random.nextInt(admins.size())))
                    .pageType(randomEnum(AdminPageType.class))
                    .actionType(randomEnum(AdminActionType.class))
                    .targetType(randomEnum(AdminTargetType.class))
                    .targetId(systemLog ? null : (long) random.nextInt(1000))
                    .resultType(random.nextBoolean()
                            ? AdminActionResult.SUCCESS
                            : AdminActionResult.FAIL)
                    .description(systemLog
                            ? "시스템 자동 처리 로그"
                            : "관리자 더미 행동 로그")
                    .createdAt(LocalDateTime.now().minusHours(random.nextInt(96)))
                    .build();

            adminActionLogRepository.save(log);
        }
    }

    /* ================= 관리자 생성 템플릿 ================= */

    private Users createAdmin(String nickname, String phone) {
        return Users.builder()
                .nickname(nickname)
                .phoneNumber(phone)
                .phoneNumberConnected(true)
                .role(Role.ROLE_ADMIN)
                .gender(Gender.MALE)           // ✅ 필수 enum 명시
                .firstLogin("SYSTEM")          // ✅ NOT NULL 가능성 대비
                .birth(LocalDate.of(1990, 1, 1))
                .suspended(false)
                .deleted(false)
                .kakaoConnected(false)
                .naverConnected(false)
                .googleConnected(false)
                .build();
    }

    private <T extends Enum<?>> T randomEnum(Class<T> clazz) {
        T[] values = clazz.getEnumConstants();
        return values[random.nextInt(values.length)];
    }
}
