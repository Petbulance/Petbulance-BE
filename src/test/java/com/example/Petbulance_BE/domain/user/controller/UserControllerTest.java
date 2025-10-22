package com.example.Petbulance_BE.domain.user.controller;

import com.example.Petbulance_BE.PetbulanceBeApplication;
import com.example.Petbulance_BE.domain.user.dto.NicknameSaveRequestDto;
import com.example.Petbulance_BE.domain.user.dto.NicknameUpdateResponseDto;
import com.example.Petbulance_BE.domain.user.dto.ProfileImageUpdateReqeustDto;
import com.example.Petbulance_BE.domain.user.entity.Users;
import com.example.Petbulance_BE.domain.user.repository.UsersJpaRepository;
import com.example.Petbulance_BE.domain.user.service.UserService;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.env.Environment;
import org.springframework.http.MediaType;
import org.junit.jupiter.api.DisplayName;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(classes = PetbulanceBeApplication.class)
@AutoConfigureMockMvc
class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UsersJpaRepository usersJpaRepository;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void nicknameAvailable() throws Exception {
        mockMvc.perform(get("/users/nickname").param("nickname", "newUser"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.nickname").value("newUser"))
                .andExpect(jsonPath("$.data.available").value(true))
                .andExpect(jsonPath("$.data.reason").doesNotExist());
    }

    @Test
    void nicknameAlreadyExists() throws Exception {
        usersJpaRepository.save(Users.builder().nickname("existingUser").build());

        mockMvc.perform(get("/users/nickname")
                        .param("nickname", "existingUser"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.nickname").value("existingUser"))
                .andExpect(jsonPath("$.data.available").value(false))
                .andExpect(jsonPath("$.data.reason").value("이미 등록된 닉네임입니다."));
    }

    @Test
    @DisplayName("유저 닉네임 저장 성공 로직")
    void nicknameSaveSuccess() throws Exception {
        String jwtToken = "eyJhbGciOiJIUzI1NiJ9.eyJ1c2VySWQiOiIzZDI3NGI5Ni03NWU0LTRkZWItYWE3YS1jMmIxZjgzODk3NzciLCJjYXRlZ29yeSI6ImFjY2VzcyIsInJvbGUiOiJST0xFX0NMSUVOVCIsImlhdCI6MTc2MDc3MzIyMywiZXhwIjoxNzYwOTUzMjIzfQ.r6i0wIkcugL9fPJHjoqdDoX9oJeVrKS3IqqXVv8Q4cc";
        NicknameSaveRequestDto nicknameSaveRequestDto = new NicknameSaveRequestDto("땅콩앵무22");
        mockMvc.perform(post("/users/nickname")
                .header("Authorization","Bearer "+jwtToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(nicknameSaveRequestDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.message").value("닉네임 저장이 완료되었습니다."));
    }

    @Test
    @DisplayName("유저 닉네임 저장 실패 로직")
    void nicknameSaveFail() throws Exception {
        String jwtToken = "eyJhbGciOiJIUzI1NiJ9.eyJ1c2VySWQiOiIzZDI3NGI5Ni03NWU0LTRkZWItYWE3YS1jMmIxZjgzODk3NzciLCJjYXRlZ29yeSI6ImFjY2VzcyIsInJvbGUiOiJST0xFX0NMSUVOVCIsImlhdCI6MTc2MDc3MzIyMywiZXhwIjoxNzYwOTUzMjIzfQ.r6i0wIkcugL9fPJHjoqdDoX9oJeVrKS3IqqXVv8Q4cc";
        NicknameSaveRequestDto nicknameSaveRequestDto = new NicknameSaveRequestDto(" ");
        mockMvc.perform(post("/users/nickname")
                        .header("Authorization","Bearer "+jwtToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(nicknameSaveRequestDto)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.data.message").value("{nickname=닉네임은 2~12자의 한글, 영문, 숫자만 사용할 수 있으며 특수문자와 띄어쓰기는 사용할 수 없습니다.}"));
    }

    @Test
    @DisplayName("유저 닉네임 수정 성공 로직")
    void nicknameUpdateSuccess() throws Exception {
        String jwtToken = "eyJhbGciOiJIUzI1NiJ9.eyJ1c2VySWQiOiIzZDI3NGI5Ni03NWU0LTRkZWItYWE3YS1jMmIxZjgzODk3NzciLCJjYXRlZ29yeSI6ImFjY2VzcyIsInJvbGUiOiJST0xFX0NMSUVOVCIsImlhdCI6MTc2MDc3MzIyMywiZXhwIjoxNzYwOTUzMjIzfQ.r6i0wIkcugL9fPJHjoqdDoX9oJeVrKS3IqqXVv8Q4cc";
        NicknameSaveRequestDto nicknameUpdateRequestDto = new NicknameSaveRequestDto("땅콩앵무");
        mockMvc.perform(patch("/users/nickname")
                        .header("Authorization","Bearer "+jwtToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(nicknameUpdateRequestDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.message").value("닉네임 수정이 완료되었습니다."));
    }

    @Test
    @DisplayName("유저 닉네임 수정 실패 로직")
    void nicknameUpdateFail() throws Exception {
        String jwtToken = "eyJhbGciOiJIUzI1NiJ9.eyJ1c2VySWQiOiIzZDI3NGI5Ni03NWU0LTRkZWItYWE3YS1jMmIxZjgzODk3NzciLCJjYXRlZ29yeSI6ImFjY2VzcyIsInJvbGUiOiJST0xFX0NMSUVOVCIsImlhdCI6MTc2MDc3MzIyMywiZXhwIjoxNzYwOTUzMjIzfQ.r6i0wIkcugL9fPJHjoqdDoX9oJeVrKS3IqqXVv8Q4cc";
        NicknameSaveRequestDto nicknameUpdateRequestDto = new NicknameSaveRequestDto(" ");
        mockMvc.perform(patch("/users/nickname")
                        .header("Authorization","Bearer "+jwtToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(nicknameUpdateRequestDto)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.data.message").value("{nickname=닉네임은 2~12자의 한글, 영문, 숫자만 사용할 수 있으며 특수문자와 띄어쓰기는 사용할 수 없습니다.}"));
    }

    @Test
    @DisplayName("유저 프로필 이미지 업데이트")
    void UserProfileImageUpdate() throws Exception {
        String jwtToken = "eyJhbGciOiJIUzI1NiJ9.eyJ1c2VySWQiOiIzZDI3NGI5Ni03NWU0LTRkZWItYWE3YS1jMmIxZjgzODk3NzciLCJjYXRlZ29yeSI6ImFjY2VzcyIsInJvbGUiOiJST0xFX0NMSUVOVCIsImlhdCI6MTc2MTExNTg4NCwiZXhwIjoxNzYxMjk1ODg0fQ.IGdJCkYKbZeVUIuya0cwKB10cvkpjm_K-bZZfmpChZI";
        ProfileImageUpdateReqeustDto profileImageUpdateRequestDto = new ProfileImageUpdateReqeustDto("image1","image/jpeg");
        mockMvc.perform(patch("/users/profile")
        .header("Authorization","Bearer "+jwtToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(profileImageUpdateRequestDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.preSignedUrl").value("presignedImage"))
                .andExpect(jsonPath("$.data.imageUrl").value("pictureUrl"));

    }

    @Test
    @DisplayName("프로필 이미지 저장 완료 검증")
    void UserProfileImageUpdate() throws Exception {
        String jwtToken = "eyJhbGciOiJIUzI1NiJ9.eyJ1c2VySWQiOiIzZDI3NGI5Ni03NWU0LTRkZWItYWE3YS1jMmIxZjgzODk3NzciLCJjYXRlZ29yeSI6ImFjY2VzcyIsInJvbGUiOiJST0xFX0NMSUVOVCIsImlhdCI6MTc2MTExNTg4NCwiZXhwIjoxNzYxMjk1ODg0fQ.IGdJCkYKbZeVUIuya0cwKB10cvkpjm_K-bZZfmpChZI";
        ProfileImageUpdateReqeustDto profileImageUpdateRequestDto = new ProfileImageUpdateReqeustDto("image1","image/jpeg");
        mockMvc.perform(patch("/users/profile")
                        .header("Authorization","Bearer "+jwtToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(profileImageUpdateRequestDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.preSignedUrl").value("presignedImage"))
                .andExpect(jsonPath("$.data.imageUrl").value("pictureUrl"));

    }
}