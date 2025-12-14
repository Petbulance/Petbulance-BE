package com.example.Petbulance_BE.domain.device.controller;

import com.example.Petbulance_BE.domain.device.dto.DeleteDeviceRequestDto;
import com.example.Petbulance_BE.domain.device.dto.DeviceAddRequestDto;
import com.example.Petbulance_BE.domain.device.service.DeviceService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/device")
@RequiredArgsConstructor
public class DeviceController {

    private final DeviceService deviceService;

    @PostMapping
    public Map<String, String> AddDevice(@RequestBody @Valid DeviceAddRequestDto deviceAddRequestDto) {
        return deviceService.AddDeviceProcess(deviceAddRequestDto);
    }

    @DeleteMapping
    public Map<String,String> deleteDevice(@RequestBody DeleteDeviceRequestDto deleteDeviceRequestDto) {
        return deviceService.deleteDeviceProcess(deleteDeviceRequestDto);
    }
}
