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
        String userId = "7d9e87b2-6c3a-4a2e-8f1d-9c4e5b6a7f80";
        log.info("{}", jwtUtil.createJwt(userId, "", "", ""));

    }

}