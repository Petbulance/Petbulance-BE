package com.example.Petbulance_BE.domain.test.controller;

import com.example.Petbulance_BE.domain.test.dto.TestResponseDto;
import com.example.Petbulance_BE.global.common.error.exception.CustomException;
import com.example.Petbulance_BE.global.common.error.exception.ErrorCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/app")
public class TestController {
    @GetMapping("/health")
    public ResponseEntity<String> health() {
        return ResponseEntity.ok("OK");
    }

    @GetMapping("/error")
    public void errorCheck() {
        throw new CustomException(ErrorCode.TEST_ERROR_CODE);
    }
}
