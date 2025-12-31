package com.example.Petbulance_BE.domain.qna.dummy;

import com.example.Petbulance_BE.domain.qna.entity.Qna;
import com.example.Petbulance_BE.domain.qna.repository.QnaRepository;
import com.example.Petbulance_BE.domain.qna.type.QnaStatus;
import com.example.Petbulance_BE.domain.user.entity.Users;
import com.example.Petbulance_BE.domain.user.repository.UsersJpaRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.Random;

@Slf4j
//@Component
@Profile({"local", "dev"})     // 🚨 운영(prod)에서는 절대 안 돌도록!
@RequiredArgsConstructor
public class QnaInitializer implements ApplicationRunner {

    private final QnaRepository qnaRepository;
    private final UsersJpaRepository usersJpaRepository;
    private final Random random = new Random();

    @Override
    public void run(ApplicationArguments args) {

        log.info("🔹 QNA Dummy 데이터 생성 시작");

        // 1️⃣ 유저 조회
        Optional<Users> user1Opt = usersJpaRepository.findById("user-000001");
        Optional<Users> user2Opt = usersJpaRepository.findById("user-000002");

        // 2️⃣ 없으면 스킵 (앱은 계속 실행)
        if (user1Opt.isEmpty() || user2Opt.isEmpty()) {
            log.warn("🚫 QNA 더미 생성 스킵 — 테스트 유저가 존재하지 않음");
            return;
        }

        Users user1 = user1Opt.get();
        Users user2 = user2Opt.get();

        List<Users> users = List.of(user1, user2);

        for (int i = 1; i <= 10; i++) {

            Users writer = users.get(random.nextInt(users.size()));

            Qna qna = Qna.builder()
                    .user(writer)
                    .title("QNA 테스트 질문 #" + i)
                    .content("테스트 질문 내용입니다. index = " + i)
                    .status(QnaStatus.ANSWER_WAITING)
                    .createdAt(LocalDateTime.now().minusDays(random.nextInt(10)))
                    .build();

            qnaRepository.save(qna);
        }

        log.info("✅ QNA 10개 생성 완료");
    }
}
