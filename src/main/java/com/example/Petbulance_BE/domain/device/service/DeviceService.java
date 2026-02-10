package com.example.Petbulance_BE.domain.device.service;

import com.example.Petbulance_BE.domain.device.dto.DeleteDeviceRequestDto;
import com.example.Petbulance_BE.domain.device.dto.DeviceAddRequestDto;
import com.example.Petbulance_BE.domain.device.entity.Device;
import com.example.Petbulance_BE.domain.device.repository.DeviceJpaRepository;
import com.example.Petbulance_BE.domain.user.entity.Users;
import com.example.Petbulance_BE.global.util.UserUtil;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
@RequiredArgsConstructor
public class DeviceService {

    private final DeviceJpaRepository deviceJpaRepository;

    @Transactional
    public Map<String, String> AddDeviceProcess(DeviceAddRequestDto deviceAddRequestDto) {
        String deviceOs = deviceAddRequestDto.getDeviceOs().toLowerCase();
        Device device = Device.builder()
                .fcm_token(deviceAddRequestDto.getFcmToken())
                .device_os(deviceOs)
                .user(UserUtil.getCurrentUser())
                .build();

        deviceJpaRepository.save(device);
        return Map.of("message", "기기가 성공적으로 등록되었습니다.");
    }

    @Transactional
    public Map<String, String> deleteDeviceProcess(DeleteDeviceRequestDto deleteDeviceRequestDto) {

        Users currentUser = UserUtil.getCurrentUser();

        deviceJpaRepository.deleteByFcmTokenAndUserId(deleteDeviceRequestDto.getFcmToken(), currentUser.getId());

        return Map.of("message", "기기 등록을 해제하였습니다.");

    }
}
