package com.example.Petbulance_BE.domain.device.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class DeviceAddRequestDto {

    @NotBlank(message = "FCM token 혹은 OS 정보가 누락되었습니다.")
    private String fcmToken;
    @NotBlank(message = "FCM token 혹은 OS 정보가 누락되었습니다.")
    private String deviceOs;
}
