package com.example.Petbulance_BE.domain.app.controller;

import com.example.Petbulance_BE.domain.app.dto.MetadataRequestDto;
import com.example.Petbulance_BE.domain.app.dto.MetadataResponseDto;
import com.example.Petbulance_BE.domain.app.service.AppService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/app")
@RequiredArgsConstructor
public class AppController {

    private final AppService appService;

    @GetMapping("/version")
    public Map<String, String> getVersion() {
        return appService.getVersionProcess();
    }

    @GetMapping("/metadata")
    public MetadataResponseDto getMetadata(@RequestBody MetadataRequestDto metadataRequestDto) {
        return appService.getMetadataProcess(metadataRequestDto);
    }
}
