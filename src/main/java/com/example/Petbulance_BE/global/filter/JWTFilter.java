package com.example.Petbulance_BE.global.filter;

import com.example.Petbulance_BE.domain.user.entity.Users;
import com.example.Petbulance_BE.domain.user.repository.UsersJpaRepository;
import com.example.Petbulance_BE.global.common.error.exception.ErrorCode;
import com.example.Petbulance_BE.global.util.CustomUserDetails;
import com.example.Petbulance_BE.global.util.JWTUtil;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
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

        String uri = request.getRequestURI();

        if (uri.startsWith("/app/health")) {
            filterChain.doFilter(request, response);
            return;
        }

        String authorization = request.getHeader("Authorization");
        if (authorization == null || !authorization.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }

        String token = authorization.split(" ")[1];
        log.info("{}", token);

        try {
            // 1. 블랙리스트 체크
            if (redisTemplate.hasKey("blackList:" + token)) {
                String userId = jwtUtil.getUserId(token);
                usersJpaRepository.findById(userId).ifPresent(users -> {
                    users.suspendUser();
                    usersJpaRepository.save(users);
                    log.info("{} 유저 잘못된 접근 계정 정지 처리", userId);
                });
                // 예외 정보를 request에 담고 전달
                request.setAttribute("exception", ErrorCode.UNAUTHORIZED_USER);
                filterChain.doFilter(request, response);
                return;
            }

            // 2. 토큰 만료 체크
            if (jwtUtil.isExpired(token)) {
                response.setHeader("X-Token-Expired", "true");
                request.setAttribute("exception", ErrorCode.ACCESS_TOKEN_EXPIRED);
                filterChain.doFilter(request, response);
                return;
            }

            // 3. 유저 검증
            String userId = jwtUtil.getUserId(token);
            Users user = usersJpaRepository.findById(userId).orElse(null);

            if (user == null) {
                request.setAttribute("exception", ErrorCode.NON_EXIST_USER);
                filterChain.doFilter(request, response);
                return;
            }

            // 4. 계정 정지 체크
            if (user.getSuspended() == Boolean.TRUE) {
                request.setAttribute("exception", ErrorCode.ACCOUNT_SUSPENDED);
                filterChain.doFilter(request, response);
                return;
            }

            // 모든 검증 통과 시 시큐리티 컨텍스트 설정
            CustomUserDetails customUserDetails = new CustomUserDetails(user);
            Authentication authToken = new UsernamePasswordAuthenticationToken(customUserDetails, null, customUserDetails.getAuthorities());
            SecurityContextHolder.getContext().setAuthentication(authToken);

        } catch (ExpiredJwtException e) {
            request.setAttribute("exception", ErrorCode.ACCESS_TOKEN_EXPIRED);
        } catch (JwtException | IllegalArgumentException e) {
            log.error("JWT 토큰 유효성 검증 실패: {}", e.getMessage());
            request.setAttribute("exception", ErrorCode.INVALID_TOKEN);
        }

        filterChain.doFilter(request, response);
    }
}