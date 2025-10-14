package com.example.Petbulance_BE.global.common.controller;

import com.example.Petbulance_BE.global.common.dto.RefreshRequestDto;
import com.example.Petbulance_BE.global.common.error.ErrorResponse;
import com.example.Petbulance_BE.global.common.error.exception.CustomException;
import com.example.Petbulance_BE.global.common.error.exception.ErrorCode;
import com.example.Petbulance_BE.global.common.redisEntity.RefreshEntity;
import com.example.Petbulance_BE.global.common.redisRepository.RefreshTokenRepository;
import com.example.Petbulance_BE.global.common.response.GlobalResponse;
import com.example.Petbulance_BE.global.util.JWTUtil;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@RestController("/auth")
public class AuthController {

    private final JWTUtil jwtUtil;
    private final RefreshTokenRepository refreshTokenRepository;

    public AuthController(JWTUtil jwtUtil, RefreshTokenRepository refreshTokenRepository) {
        this.jwtUtil = jwtUtil;
        this.refreshTokenRepository = refreshTokenRepository;
    }

    @GetMapping("logout")
    public GlobalResponse<?> logoutProcess() {
        return GlobalResponse.success(200, "logout success");
    }

    @PostMapping("/refresh")
    public Map<String,String> refreshProcess(@RequestBody RefreshRequestDto refreshRequestDto) {
        String refreshToken = refreshRequestDto.getRefreshToken();
        String userId = jwtUtil.getUserId(refreshToken);
        String role = jwtUtil.getRole(refreshToken);
        Optional<RefreshEntity> optionalEntity = refreshTokenRepository.findByUserId(userId);
        if(optionalEntity.isPresent()) {

            refreshTokenRepository.delete(optionalEntity.get());

            String accessToken = jwtUtil.createJwt(userId,"access", role);
            String refresh = jwtUtil.createJwt(userId,"refresh", role);

            RefreshEntity refreshEntity = new RefreshEntity(userId, refresh, 8640000000L);
            refreshTokenRepository.save(refreshEntity);

            Map<String, String> tokenResponse = new HashMap<>();

            tokenResponse.put("accessToken", "Bearer " + accessToken);
            tokenResponse.put("refreshToken", refresh);

            return tokenResponse;
        } else {
            throw new CustomException(ErrorCode.NON_EXIST_REFRESH_TOKEN);
        }

    }

}
