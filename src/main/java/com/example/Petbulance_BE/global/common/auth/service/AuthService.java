package com.example.Petbulance_BE.global.common.auth.service;

import com.example.Petbulance_BE.domain.dashboard.service.DashboardMetricRedisService;
import com.example.Petbulance_BE.domain.user.entity.Users;
import com.example.Petbulance_BE.domain.user.repository.UsersJpaRepository;
import com.example.Petbulance_BE.domain.userEmail.entity.UserEmails;
import com.example.Petbulance_BE.domain.userEmail.repository.UserEmailsJpaRepository;
import com.example.Petbulance_BE.domain.userSetting.entity.UserAuthority;
import com.example.Petbulance_BE.domain.userSetting.entity.UserSetting;
import com.example.Petbulance_BE.global.common.auth.component.RandomNicknameGenerator;
import com.example.Petbulance_BE.global.common.dto.LoginRequestDto;
import com.example.Petbulance_BE.global.common.dto.LoginResponseDto;
import com.example.Petbulance_BE.global.common.error.exception.CustomException;
import com.example.Petbulance_BE.global.common.error.exception.ErrorCode;
import com.example.Petbulance_BE.global.common.redisEntity.RefreshEntity;
import com.example.Petbulance_BE.global.common.redisRepository.RefreshTokenRepository;
import com.example.Petbulance_BE.global.common.type.Role;
import com.example.Petbulance_BE.global.firebase.FirebaseTokenService;
import com.example.Petbulance_BE.global.util.JWTUtil;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.firebase.auth.FirebaseAuthException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class AuthService {

    private final UsersJpaRepository usersJpaRepository;
    private final UserEmailsJpaRepository userEmailsJpaRepository;
    private final JWTUtil jwtUtil;
    private final RefreshTokenRepository refreshTokenRepository;
    private final FirebaseTokenService firebaseTokenService;
    private final RestTemplate restTemplate = new RestTemplate();
    private final RandomNicknameGenerator randomNicknameGenerator;
    private final DashboardMetricRedisService dashboardMetricRedisService;

    public LoginResponseDto loginService(LoginRequestDto dto) {
        String provider = dto.getProvider().toUpperCase();
        String email;

        switch (provider) {
            case "KAKAO" -> email = getKakaoEmail(dto.getAuthCode());
            case "NAVER" -> email = getNaverEmail(dto.getAuthCode());
            case "GOOGLE" -> email = getGoogleEmail(dto.getAuthCode());
            default -> throw new CustomException(ErrorCode.INVALID_PROVIDER);
        }

        final boolean[] isNewUser = {false};

        Users user = userEmailsJpaRepository.findByEmailByProvider(provider, email)
                .map(u -> {
                    switch (provider) {
                        case "KAKAO" -> { if (!u.getKakaoConnected()) u.setKakaoConnected(true); }
                        case "NAVER" -> { if (!u.getNaverConnected()) u.setNaverConnected(true); }
                        case "GOOGLE" -> { if (!u.getGoogleConnected()) u.setGoogleConnected(true); }
                    }
                    return u;
                })
                .orElseGet(() -> {
                    isNewUser[0] = true;
                    return createNewUser(provider, email);
                });

        if (isNewUser[0]) {
            dashboardMetricRedisService.incrementTodaySignup();
        }

        LoginResponseDto response = createLoginResponse(user, provider);
        response.setIsNewUser(isNewUser[0]);
        return response;
    }

    private String getKakaoEmail(String accessToken) {
        HttpHeaders headers = new HttpHeaders();
        headers.add("Authorization", "Bearer " + accessToken);
        headers.add("Content-type", "application/x-www-form-urlencoded;charset=utf-8");

        ResponseEntity<String> response = restTemplate.exchange(
                "https://kapi.kakao.com/v2/user/me",
                HttpMethod.POST,
                new HttpEntity<>(headers),
                String.class
        );

        try {
            JsonNode node = new ObjectMapper().readTree(response.getBody());
            return node.path("kakao_account").path("email").asText();
        } catch (Exception e) {
            throw new CustomException(ErrorCode.FAIL_KAKAO_LOGIN);
        }
    }

    private String getNaverEmail(String accessToken) {
        HttpHeaders headers = new HttpHeaders();
        headers.add("Authorization", "Bearer " + accessToken);

        ResponseEntity<String> response = restTemplate.exchange(
                "https://openapi.naver.com/v1/nid/me",
                HttpMethod.GET,
                new HttpEntity<>(headers),
                String.class
        );

        try {
            JsonNode node = new ObjectMapper().readTree(response.getBody());
            return node.path("response").path("email").asText();
        } catch (Exception e) {
            throw new CustomException(ErrorCode.FAIL_NAVER_LOGIN);
        }
    }

    private String getGoogleEmail(String accessToken) {
        HttpHeaders headers = new HttpHeaders();
        headers.add("Authorization", "Bearer " + accessToken);

        ResponseEntity<String> response = restTemplate.exchange(
                "https://www.googleapis.com/oauth2/v2/userinfo",
                HttpMethod.GET,
                new HttpEntity<>(headers),
                String.class
        );

        try {
            JsonNode node = new ObjectMapper().readTree(response.getBody());
            return node.path("email").asText();
        } catch (Exception e) {
            throw new CustomException(ErrorCode.FAIL_GOOGLE_LOGIN);
        }
    }

    private Users createNewUser(String provider, String email) {

        String nickname;
        do{
            nickname = randomNicknameGenerator.generateNickname();
        } while (usersJpaRepository.existsByNickname(nickname));

        UserSetting setting = UserSetting.builder()
                .totalPush(false)
                .eventPush(false)
                .marketingPush(false)
                .build();

        UserAuthority authority = UserAuthority.builder()
                .locationService(false)
                .marketing(false)
                .camera(false)
                .build();

        Users newUser = Users.builder()
                .nickname(nickname)
                .firstLogin(provider)
                .role(Role.ROLE_TEMPORAL)
                .profileImage("default_image/159833.png")
                .userSetting(new ArrayList<>(List.of(setting)))
                .createdAt(LocalDateTime.now())
                .userAuthority(authority)
                .build();

        setting.setUser(newUser);


        usersJpaRepository.save(newUser);

        UserEmails emails = UserEmails.builder()
                .user(newUser)
                .build();

        switch (provider) {
            case "KAKAO" -> emails.setKakaoEmail(email);
            case "NAVER" -> emails.setNaverEmail(email);
            case "GOOGLE" -> emails.setGoogleEmail(email);
        }

        userEmailsJpaRepository.save(emails);


        switch (provider) {
            case "KAKAO" -> newUser.setKakaoConnected(true);
            case "NAVER" -> newUser.setNaverConnected(true);
            case "GOOGLE" -> newUser.setGoogleConnected(true);
        }

        return newUser;
    }

    private LoginResponseDto createLoginResponse(Users user, String provider) {
        String userId = user.getId();
        String role = user.getRole().name();

        String access = jwtUtil.createJwt(userId, "access", role, provider);
        String refresh = jwtUtil.createJwt(userId, "refresh", role, provider);

        refreshTokenRepository.save(new RefreshEntity(userId, refresh, 8640000000L));

        String firebaseCustomToken;
        try {
            firebaseCustomToken = firebaseTokenService.createCustomToken(userId);
        } catch (FirebaseAuthException e) {
            throw new CustomException(ErrorCode.FirebaseToken_Fail);
        }

        return new LoginResponseDto(false, firebaseCustomToken, access, refresh);
    }
}


