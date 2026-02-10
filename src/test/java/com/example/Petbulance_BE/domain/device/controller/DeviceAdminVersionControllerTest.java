package com.example.Petbulance_BE.domain.device.controller;

import com.example.Petbulance_BE.domain.device.dto.DeleteDeviceRequestDto;
import com.example.Petbulance_BE.domain.device.dto.DeviceAddRequestDto;
import com.example.Petbulance_BE.domain.device.service.DeviceService;
import com.example.Petbulance_BE.global.util.JWTUtil;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class DeviceAdminVersionControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private JWTUtil jwtUtil;

    @Autowired
    private DeviceService deviceService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    @DisplayName("알림 수신 기기 등록")
    void addDeviceTest() throws Exception {
        String jwt = jwtUtil.createJwt("3a7a6eba-f107-42b5-8e2d-4536a94a17bf", "access", "ROLE_CLIENT", "GOOGLE");
        DeviceAddRequestDto deviceAddRequestDto = new DeviceAddRequestDto("fcm token example", "android");
        mockMvc.perform(post("/device/device")
                        .header("Authorization","Bearer "+jwt)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(deviceAddRequestDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.message").value("기기가 성공적으로 등록되었습니다."));
    }

    @Test
    @DisplayName("등록 기기 삭제 테스트 코드")
    void check() throws Exception{
        String jwt = jwtUtil.createJwt("3d274b96-75e4-4deb-aa7a-c2b1f8389777", "access", "ROLE_CLIENT", "GOOGLE");
        DeleteDeviceRequestDto n = new DeleteDeviceRequestDto("1");
        mockMvc.perform(delete("/device")
                        .header("Authorization","Bearer "+jwt)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(n)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.message").value("기기 등록을 해제하였습니다."));
    }
}