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
        String userId = "533e8700-ef36-4f61-8b4b-51f1135ea6fe";
        log.info("{}", jwtUtil.createJwt(userId, "", "", ""));

    }

}