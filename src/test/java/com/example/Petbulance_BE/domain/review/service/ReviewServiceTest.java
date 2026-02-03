package com.example.Petbulance_BE.domain.review.service;

import com.example.Petbulance_BE.global.util.JWTUtil;
import com.example.Petbulance_BE.global.util.UserUtil;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Slf4j
class ReviewServiceTest {

    @Autowired
    JWTUtil jwtUtil;

    @Test
    public void makeJwt () {
        String userId = "0e86c227-2ec5-4abd-832a-72b9bd7a5f59";
        log.info("{}", jwtUtil.createJwt(userId, "access", "ROLE_TEMPORAL", "NAVER"));

    }

}