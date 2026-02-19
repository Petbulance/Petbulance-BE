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
        String userId = "0e86c227-2ec5-4abd-832a-72b9bd7a5f59";
        log.info("{}", jwtUtil.createJwt(userId, "access", "ROLE_ADMIN", "NAVER"));

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