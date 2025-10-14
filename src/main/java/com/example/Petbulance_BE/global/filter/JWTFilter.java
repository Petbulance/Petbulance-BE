package com.example.Petbulance_BE.global.filter;

import com.example.Petbulance_BE.domain.user.entity.Users;
import com.example.Petbulance_BE.domain.user.repository.UsersJpaRepository;
import com.example.Petbulance_BE.global.common.type.Role;
import com.example.Petbulance_BE.global.util.CustomUserDetails;
import com.example.Petbulance_BE.global.util.JWTUtil;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
@Slf4j
public class JWTFilter extends OncePerRequestFilter {

    private final JWTUtil jwtUtil;
    private final StringRedisTemplate redisTemplate;
    private final UsersJpaRepository usersJpaRepository;

    public JWTFilter(JWTUtil jwtUtil, StringRedisTemplate redisTemplate, UsersJpaRepository usersJpaRepository) {
        this.jwtUtil = jwtUtil;
        this.redisTemplate = redisTemplate;
        this.usersJpaRepository = usersJpaRepository;
    }


    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {


        String authorization = request.getHeader("Authorization");
        //헤더 Authentication에 jwt토큰이 존재하지 않는 경우
        if (authorization == null || !authorization.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }

        String token = authorization.split(" ")[1];

        //로그아웃한 유저의 jwt토큰으로 접근시 유저 계정 정지
        if (redisTemplate.hasKey("blackList:" + token)) {
            String userId = jwtUtil.getUserId(token);
            usersJpaRepository.findById(userId).ifPresent(users -> {
                users.suspendUser();
                usersJpaRepository.save(users);
                log.info("{} 유저 잘못된 접근 계정 정지 처리", userId);
            });
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            return;
        }

        //토큰 만료시(재로그인 로직 추가시 예외 수정 필요)
        if (jwtUtil.isExpired(token)) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            return;
        }

        //접근 검증 완료시
        String userId = jwtUtil.getUserId(token);
        String role = jwtUtil.getRole(token);

        Users user = Users.builder()
                .id(userId)
                .role(Role.valueOf(role))
                .build();


        CustomUserDetails customUserDetails = new CustomUserDetails(user);

        Authentication authToken = new UsernamePasswordAuthenticationToken(customUserDetails, null, customUserDetails.getAuthorities());

        SecurityContextHolder.getContext().setAuthentication(authToken);

        filterChain.doFilter(request, response);
    }


}
