package com.example.Petbulance_BE.global.security;

import com.example.Petbulance_BE.global.filter.JWTFilter;
import com.example.Petbulance_BE.global.filter.LogoutFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;


import java.util.List;

import static org.springframework.security.config.Customizer.withDefaults;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private final JWTFilter jwtFilter;
    private final LogoutFilter logoutFilter;

    public SecurityConfig(LogoutFilter logoutFilter, JWTFilter jwtFilter) {
        this.jwtFilter = jwtFilter;
        this.logoutFilter = logoutFilter;
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
    public SecurityFilterChain filterChain(HttpSecurity http, AuthenticationConfiguration authenticationConfiguration) throws Exception {

        http
                .cors(cors -> cors.configurationSource(corsConfigurationSource()));
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
                       // .requestMatchers("/admin/**").hasRole("ADMIN")
                        .anyRequest().authenticated()
                );
        http
                .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class);
      /*  http
                .oauth2Login((oauth2) -> oauth2
                        .userInfoEndpoint((userInfoEndpointConfig) -> userInfoEndpointConfig
                                .userService(customOAuth2UserService))
                        .successHandler(customSuccessHandler)
                );*/
        http
                .addFilterBefore(logoutFilter, UsernamePasswordAuthenticationFilter.class);



        return http.build();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();

        configuration.setAllowCredentials(true);

        configuration.setAllowedOriginPatterns(List.of(
                "http://localhost:5173",
                "http://localhost:5173/",
                "http://127.0.0.1:5173/",
                "https://127.0.0.1:5173",
                "http://petbulance.cloud",
                "http://petbulance.cloud/",
                "http://admin.petbulance.co.kr",
                "http://admin.petbulance.co.kr/",
                "https://www.petbulance.co.kr/",
                "https://www.petbulance.co.kr",
                "https://www.admin.petbulance.co.kr/",
                "https://www.admin.petbulance.co.kr",
                "http://petbulance.local:5173",
                "http://petbulance.local:5173/",
                "https://petbulance.local:5173",
                "https://petbulance.local:5173/",
                "http://admin.petbulance.local:5173",
                "http://admin.petbulance.local:5173/"
        ));

        configuration.setAllowedMethods(List.of(
                "GET","POST","PUT","PATCH","DELETE","OPTIONS"
        ));

        configuration.setAllowedHeaders(List.of("*"));

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }

}
