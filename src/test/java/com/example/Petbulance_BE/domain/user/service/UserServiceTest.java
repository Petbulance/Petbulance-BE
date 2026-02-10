package com.example.Petbulance_BE.domain.user.service;

import com.example.Petbulance_BE.domain.user.dto.response.NicknameResponseDto;
import com.example.Petbulance_BE.domain.user.entity.Users;
import com.example.Petbulance_BE.domain.user.repository.UsersJpaRepository;
import jakarta.transaction.Transactional;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest
@Transactional
@ActiveProfiles("test")
class UserServiceTest {

    @Autowired
    private UserService userService;

    @Autowired
    private UsersJpaRepository usersJpaRepository;

    @Test
    @DisplayName("닉네임 조회 실패 케이스")
    void nicknameFail() {
        //given
        String nickname = "김영욱";
        usersJpaRepository.save(Users.builder()
                .nickname(nickname)
                .build());

        //when
        NicknameResponseDto result = userService.checkNicknameProcess(nickname);

        //then
        Assertions.assertThat(result.getAvailable()).isFalse();
        Assertions.assertThat(result.getReason()).isEqualTo("이미 등록된 닉네임입니다.");
    }





}