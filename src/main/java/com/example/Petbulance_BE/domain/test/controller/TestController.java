package com.example.Petbulance_BE.domain.test.controller;

import com.example.Petbulance_BE.domain.test.dto.TestResponseDto;
import com.example.Petbulance_BE.global.common.error.exception.CustomException;
import com.example.Petbulance_BE.global.common.error.exception.ErrorCode;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/app")
public class TestController {
    @GetMapping("/health")
    public TestResponseDto healthCheck() {
        return new TestResponseDto("테스트에 성공하였습니다!!!!");
    }

    @GetMapping("/error")
    public void errorCheck() {
        throw new CustomException(ErrorCode.TEST_ERROR_CODE);
    }
}
