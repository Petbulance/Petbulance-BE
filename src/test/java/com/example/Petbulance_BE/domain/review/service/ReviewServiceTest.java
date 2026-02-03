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
        String userId = "8c722002-8507-4a49-a801-722e905b3b4a";
        log.info("{}", jwtUtil.createJwt(userId, "access", "ROLE_ADMIN", "NAVER"));

    }

}