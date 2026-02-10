package com.example.Petbulance_BE.domain.inquiry.dummy;

import com.example.Petbulance_BE.domain.inquiry.entity.Inquiry;
import com.example.Petbulance_BE.domain.inquiry.repository.InquiryRepository;
import com.example.Petbulance_BE.domain.inquiry.type.InquiryAnswerType;
import com.example.Petbulance_BE.domain.inquiry.type.InquiryType;
import com.example.Petbulance_BE.domain.inquiry.type.InterestType;
import com.example.Petbulance_BE.domain.user.entity.Users;
import com.example.Petbulance_BE.domain.user.repository.UsersJpaRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Random;


@Slf4j
//@Component
@Profile("prod")
@RequiredArgsConstructor
public class InquiryInitializer implements ApplicationRunner {

    private final InquiryRepository inquiryRepository;
    private final UsersJpaRepository userRepository;

    private final Random random = new Random();

    @Override
    public void run(ApplicationArguments args) {

        // 이미 존재하면 생성 안 함 (1번만 생성)
        if (inquiryRepository.count() > 0) {
            log.info("⏸ Inquiry 데이터가 이미 존재 — 생성 스킵");
            return;
        }

        log.info("🔹 Inquiry 더미 생성 시작");

        // 유저 — 두 명 있다고 가정
        Users user1 = userRepository.findById("user-000001").orElseThrow();
        Users user2 = userRepository.findById("user-000002").orElseThrow();

        List<Users> users = List.of(user1, user2);

        InquiryType[] inquiryTypes = InquiryType.values();
        InterestType[] interestTypes = InterestType.values();
        InquiryAnswerType[] answerTypes = InquiryAnswerType.values();

        for (int i = 1; i <= 10; i++) {

            Users writer = users.get(random.nextInt(users.size()));

            Inquiry inquiry = Inquiry.builder()
                    .user(writer)
                    .type(inquiryTypes[random.nextInt(inquiryTypes.length)])
                    .companyName("테스트 회사 #" + i)
                    .managerName("담당자_" + i)
                    .managerPosition("매니저")
                    .phone("010-1234-00" + i)
                    .email("test" + i + "@mail.com")
                    .interestType(interestTypes[random.nextInt(interestTypes.length)])
                    .content("테스트 문의 내용입니다. index = " + i)
                    .privacyConsent(true)
                    .answerContent("")                 // 기본 미답변
                    .answeredAt(null)                 // 기본 null
                    .inquiryAnswerType(answerTypes[random.nextInt(answerTypes.length)])          // 미답변 상태
                    .build();

            // 30% 정도 확률로 “답변 완료” 처리
            if (random.nextInt(10) < 3) {
                inquiry.answer("문의에 대한 답변입니다. #" + i);
            }

            inquiryRepository.save(inquiry);
        }

        log.info("✅ Inquiry 40개 생성 완료");
    }
}

