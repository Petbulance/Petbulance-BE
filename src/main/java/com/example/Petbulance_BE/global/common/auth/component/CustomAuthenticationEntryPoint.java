package com.example.Petbulance_BE.global.common.auth.component;

import com.example.Petbulance_BE.global.common.error.exception.ErrorCode;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

@Component
public class CustomAuthenticationEntryPoint implements AuthenticationEntryPoint {

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException authException) throws IOException, ServletException {
        // 1. 필터에서 넘겨준 에러 코드를 가져옴
        ErrorCode errorCode = (ErrorCode) request.getAttribute("exception");

        // 2. 만약 직접적인 에러 코드가 없다면 (인가 실패 등) 기본 401 설정
        if (errorCode == null) {
            errorCode = ErrorCode.ACCESS_TOKEN_EXPIRED; // 혹은 별도의 UNAUTHORIZED 코드
        }

        // 3. ErrorCode에 정의된 HttpStatus 숫자를 가져옴 (400 또는 401 등)
        int statusValue = errorCode.getStatus().value();

        response.setContentType("application/json;charset=utf-8");
        response.setStatus(statusValue); // HTTP 응답 헤더 상태 코드 설정

        // 4. 요구하신 JSON 형식 구성
        Map<String, Object> errorDetails = new HashMap<>();
        errorDetails.put("errorClassName", errorCode.name());
        errorDetails.put("message", errorCode.getMessage());

        Map<String, Object> errorBody = new LinkedHashMap<>();
        errorBody.put("status", statusValue); // JSON 바디의 status도 동적으로 설정
        errorBody.put("success", false);
        errorBody.put("error", errorDetails);

        response.getWriter().write(objectMapper.writeValueAsString(errorBody));
    }
}
