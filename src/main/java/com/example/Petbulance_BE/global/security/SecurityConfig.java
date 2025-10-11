package com.example.Petbulance_BE.global.security;

import com.example.Petbulance_BE.domain.user.repository.UsersJpaRepository;
import com.example.Petbulance_BE.global.common.redisRepository.RefreshTokenRepository;
import com.example.Petbulance_BE.global.filter.JWTFilter;
import com.example.Petbulance_BE.global.oauth2.custom.CustomOAuth2UserService;
import com.example.Petbulance_BE.global.oauth2.custom.CustomSuccessHandler;
import com.example.Petbulance_BE.global.util.JWTUtil;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private final JWTUtil jwtUtil;
    private final RedisTemplate<String, String> redisTemplate;
    private final UsersJpaRepository usersJpaRepository;
    private final RefreshTokenRepository refreshTokenRepository;

    public SecurityConfig(JWTUtil jwtUtil, RedisTemplate<String, String> redisTemplate, UsersJpaRepository usersJpaRepository, RefreshTokenRepository refreshTokenRepository) {
        this.jwtUtil = jwtUtil;
        this.redisTemplate = redisTemplate;
        this.usersJpaRepository = usersJpaRepository;
        this.refreshTokenRepository = refreshTokenRepository;
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration configuration) throws Exception {
        return configuration.getAuthenticationManager();
    }

    @Bean
    public BCryptPasswordEncoder bCryptPasswordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public CustomSuccessHandler customSuccessHandler(JWTUtil jwtUtil){
        return new CustomSuccessHandler(jwtUtil, refreshTokenRepository);
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http, AuthenticationConfiguration authenticationConfiguration, CustomOAuth2UserService customOAuth2UserService, CustomSuccessHandler customSuccessHandler) throws Exception {

        http
                .csrf((auth)->auth.disable());
        http
                .formLogin((auth)->auth.disable());
        http
                .httpBasic((auth)->auth.disable());
        http
                .sessionManagement((session)->session.sessionCreationPolicy(SessionCreationPolicy.STATELESS));
        http
                .authorizeHttpRequests((auth)-> auth
                    .requestMatchers(
                            "/**"
                    ).permitAll()
                        .requestMatchers("/client/**").hasRole("CLIENT")
                        .requestMatchers("/admin/**").hasRole("ADMIN")
                        .anyRequest().authenticated()
                );
        http
                .addFilterBefore(new JWTFilter(jwtUtil, redisTemplate, usersJpaRepository), UsernamePasswordAuthenticationFilter.class);
        http
                .oauth2Login((oauth2) -> oauth2
                        .userInfoEndpoint((userInfoEndpointConfig) -> userInfoEndpointConfig
                                .userService(customOAuth2UserService))
                        .successHandler(customSuccessHandler(jwtUtil)) // <-- 여기서 직접 생성한 빈을 사용
                );


        return http.build();
    }
}
