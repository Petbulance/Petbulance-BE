package com.example.Petbulance_BE.domain.device.controller;

import com.example.Petbulance_BE.domain.device.dto.DeleteDeviceReqeustDto;
import com.example.Petbulance_BE.domain.device.dto.DeviceAddReqeustDto;
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
    public Map<String, String> AddDevice(@RequestBody @Valid DeviceAddReqeustDto deviceAddReqeustDto) {
        return deviceService.AddDeviceProcess(deviceAddReqeustDto);
    }

    @DeleteMapping
    public Map<String,String> deleteDevice(@RequestBody DeleteDeviceReqeustDto deleteDeviceReqeustDto) {
        return deviceService.deleteDeviceProcess(deleteDeviceReqeustDto);
    }
}
