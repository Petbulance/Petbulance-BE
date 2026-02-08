package com.example.Petbulance_BE.domain.review.service;

import com.example.Petbulance_BE.global.common.s3.S3Service;
import com.example.Petbulance_BE.global.util.JWTUtil;
import com.example.Petbulance_BE.global.util.UserUtil;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.net.URL;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Slf4j
class ReviewServiceTest {

    @Autowired
    JWTUtil jwtUtil;
    @Autowired
    S3Service s3Service;

    @Test
    public void makeJwt () {
        String userId = "3a7a6eba-f107-42b5-8e2d-4536a94a17bf";
        log.info("{}", jwtUtil.createJwt(userId, "access", "ROLE_CLIENT", "NAVER"));

    }

    @Test
    public void makeUUID () {
        String string = UUID.randomUUID().toString();
        log.info("{}", string);
    }

    @Test
    public void makeImagePre(){
        URL presignedPutUrl = s3Service.createPresignedPutUrl("default_image/159833.png", "image/png", 500000);
        log.info("{}", presignedPutUrl);
    }

    @Test
    public void makeImagePost(){
        boolean b = s3Service.doesObjectExist("default_image/159833.png");
        log.info("{}", b);
    }

}