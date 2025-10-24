package com.example.Petbulance_BE.domain.app.controller;

import com.example.Petbulance_BE.domain.app.dto.MetadataRequestDto;
import com.example.Petbulance_BE.domain.app.repository.AppsJpaRepository;
import com.example.Petbulance_BE.domain.category.repository.CategoryJpaRepository;
import com.example.Petbulance_BE.domain.region1.repository.Region1JpaRepository;
import com.example.Petbulance_BE.domain.region2.repository.Region2JpaRepository;
import com.example.Petbulance_BE.domain.species.repository.SpeciesJpaRepository;
import com.example.Petbulance_BE.global.util.JWTUtil;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class AppControllerTest {

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private JWTUtil jwtUtil;
    @Autowired
    private AppsJpaRepository appRepository;
    @Autowired
    private Region1JpaRepository region1JpaRepository;
    @Autowired
    private Region2JpaRepository region2JpaRepository;
    @Autowired
    private SpeciesJpaRepository speciesJpaRepository;
    @Autowired
    private CategoryJpaRepository categoryJpaRepository;
    @Autowired
    private AppsJpaRepository appsJpaRepository;

    @Test
    @DisplayName("앱 버전 정보 조회")
    void myInfo() throws Exception {
        String jwt = jwtUtil.createJwt("3a7a6eba-f107-42b5-8e2d-4536a94a17bf", "access", "ROLE_CLIENT", "GOOGLE");
        mockMvc.perform(get("/app/version")
                        .header("Authorization","Bearer "+jwt))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.version").value("v2.0"));

    }

    @Test
    @DisplayName("데이터 동기화 여부 확인")
    void checkVersion() throws Exception {
        MetadataRequestDto requestDto = new MetadataRequestDto("r1.6","s1.6","c1.5");
        String jwt = jwtUtil.createJwt("3a7a6eba-f107-42b5-8e2d-4536a94a17bf", "access", "ROLE_CLIENT", "GOOGLE");
        mockMvc.perform(get("/app/metadata")
        .header("Authorization","Bearer "+jwt)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.version").value("v2.0"));
    }

}