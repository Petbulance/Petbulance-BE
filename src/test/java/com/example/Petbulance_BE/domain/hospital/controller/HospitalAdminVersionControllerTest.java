package com.example.Petbulance_BE.domain.hospital.controller;

import com.example.Petbulance_BE.domain.hospital.service.HospitalService;
import com.example.Petbulance_BE.global.util.JWTUtil;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class HospitalAdminVersionControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private JWTUtil jwtUtil;

    @Autowired
    private HospitalService hospitalService;

    @Autowired
    private ObjectMapper objectMapper;

    @DisplayName("병원 조회 페이지")
    @Test
    void getHospital() throws Exception {
        String jwt = jwtUtil.createJwt("a67b9653-e827-435b-9acf-59ca1df10715", "access", "ROLE_CLIENT", "NAVER");
        mockMvc.perform(get("/hospitals")
        .header("Authorization", "Bearer " + jwt)
                        .param("q","서울")
                        .param("region", "서울특별시 강남구")
                        .param("animal","FISH","BIRDS")
        )
                .andExpect(status().isBadRequest());

    }

}