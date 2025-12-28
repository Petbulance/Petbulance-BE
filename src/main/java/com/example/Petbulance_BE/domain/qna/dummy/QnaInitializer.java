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
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Random;

@Slf4j
//@Component
@RequiredArgsConstructor
public class QnaInitializer implements ApplicationRunner {

    private final QnaRepository qnaRepository;
    private final UsersJpaRepository usersJpaRepository;

    private final Random random = new Random();

    @Override
    public void run(ApplicationArguments args) {

        log.info("🔹 QNA Dummy 데이터 생성 시작");

        // 유저 2명만 가져온다고 가정
        Users user1 = usersJpaRepository.findById("user-000001").orElseThrow();
        Users user2 = usersJpaRepository.findById("user-000002").orElseThrow();

        List<Users> users = List.of(user1, user2);

        for (int i = 1; i <= 40; i++) {

            Users writer = users.get(random.nextInt(users.size()));

            // 기본 QNA 생성 (답변 전 상태)
            Qna qna = Qna.builder()
                    .user(writer)
                    .title("QNA 테스트 질문 #" + i)
                    .content("테스트 질문 내용입니다. index = " + i)
                    .status(QnaStatus.ANSWER_WAITING)
                    .createdAt(LocalDateTime.now().minusDays(random.nextInt(10)))
                    .build();

            qnaRepository.save(qna);
        }

        log.info("✅ QNA 40개 생성 완료");
    }
}
