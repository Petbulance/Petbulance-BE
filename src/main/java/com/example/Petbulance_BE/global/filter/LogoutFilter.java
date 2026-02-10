package com.example.Petbulance_BE.global.filter;

import com.example.Petbulance_BE.global.util.JWTUtil;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.GenericFilterBean;

import java.io.IOException;
import java.util.Date;
import java.util.concurrent.TimeUnit;

@Component
@Slf4j
public class LogoutFilter extends GenericFilterBean {

    private final JWTUtil jwtUtil;
    private final StringRedisTemplate redisTemplate;

    public LogoutFilter(JWTUtil jwtUtil, StringRedisTemplate redisTemplate ) {
        this.jwtUtil = jwtUtil;
        this.redisTemplate = redisTemplate;

    }

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        doFilter((HttpServletRequest) servletRequest, (HttpServletResponse) servletResponse, filterChain);
    }

    private void doFilter(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String requestUri = request.getRequestURI();
        if(!requestUri.matches("^/auth/logout$")){
            filterChain.doFilter(request, response);
            return;
        }

        String requestMethod = request.getMethod();
        if(!requestMethod.equals("GET")){
            filterChain.doFilter(request, response);
            return;
        }

        String authorization = request.getHeader("Authorization");

        String accessToken = authorization.split(" ")[1];

        Date expiration = jwtUtil.getExpiration(accessToken);
        long now = System.currentTimeMillis();
        long expTime = expiration.getTime();
        long diff = (expTime - now)/1000;

        redisTemplate.opsForValue().set("blackList:"+accessToken, "logout", diff, TimeUnit.SECONDS);

        filterChain.doFilter(request, response);
    }
}
