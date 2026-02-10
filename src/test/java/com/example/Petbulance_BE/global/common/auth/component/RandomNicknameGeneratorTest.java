package com.example.Petbulance_BE.global.common.auth.component;

import jakarta.transaction.Transactional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
@ActiveProfiles("test")
class RandomNicknameGeneratorTest {

    @Autowired
    private RandomNicknameGenerator randomNicknameGenerator;

    @Test
    @DisplayName("닉네임 랜덤 생성 테스트 코드")
    public void creatRandomNickname(){
        String s = randomNicknameGenerator.generateNickname();
        System.out.println(s);
    }

}