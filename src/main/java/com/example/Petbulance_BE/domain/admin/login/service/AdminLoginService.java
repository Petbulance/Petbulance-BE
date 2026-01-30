package com.example.Petbulance_BE.domain.admin.login.service;

import com.example.Petbulance_BE.domain.admin.login.dto.AdminLoginReqDto;
import com.example.Petbulance_BE.domain.admin.login.dto.AdminRegisterReqDto;
import com.example.Petbulance_BE.domain.user.entity.Users;
import com.example.Petbulance_BE.domain.user.repository.UsersJpaRepository;
import com.example.Petbulance_BE.global.common.error.exception.CustomException;
import com.example.Petbulance_BE.global.common.error.exception.ErrorCode;
import com.example.Petbulance_BE.global.common.type.Role;
import com.example.Petbulance_BE.global.util.JWTUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.Map;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class AdminLoginService {

    private final UsersJpaRepository usersJpaRepository;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;
    private final JWTUtil jwtUtil;

    public Map<String, String> adminLoginProcess(AdminLoginReqDto adminLoginReqDto) {

        String username = adminLoginReqDto.getUsername();
        String password = adminLoginReqDto.getPassword();

        System.out.println("비밀번호: " + bCryptPasswordEncoder.encode(password));

        Users user = usersJpaRepository.findByNickname(username).orElseThrow(() -> new CustomException(ErrorCode.INVALID_LOGIN_CREDENTIALS));
        if (!bCryptPasswordEncoder.matches(password, user.getPassword())) {
            throw new CustomException(ErrorCode.INVALID_LOGIN_CREDENTIALS);
        }

        String jwt = jwtUtil.createAdminJwt(user.getId(), "access", user.getRole().name());

        return Map.of("access_token", jwt);

    }

    @Transactional
    public Map<String, String> adminRegisterProcess(AdminRegisterReqDto adminRegisterReqDto) {

        String nickname = adminRegisterReqDto.getNickname();
        String password = adminRegisterReqDto.getPassword();

        String encodePW = bCryptPasswordEncoder.encode(password);

        if(usersJpaRepository.existsByNickname(nickname)) throw new CustomException(ErrorCode.FAIL_READ_RANDOM_NICKNAME_FILE);

        Users user = Users.builder()
                .nickname(nickname)
                .password(encodePW)
                .role(Role.ROLE_ADMIN)
                .suspended(false)
                .deleted(false)
                .build();

        usersJpaRepository.save(user);

        return Map.of("message", "success");

    }
}
