package com.example.Petbulance_BE.domain.app.controller;

import com.example.Petbulance_BE.domain.app.dto.MetadataRequestDto;
import com.example.Petbulance_BE.domain.app.dto.MetadataResponseDto;
import com.example.Petbulance_BE.domain.app.dto.request.GetPresignReqDto;
import com.example.Petbulance_BE.domain.app.dto.response.GetPresignResDto;
import com.example.Petbulance_BE.domain.app.service.AppService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

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
    public MetadataResponseDto getMetadata(@ModelAttribute MetadataRequestDto metadataRequestDto) {
        return appService.getMetadataProcess(metadataRequestDto);
    }

    @PostMapping("/image/presign")
    public GetPresignResDto getPresignedUrl(@RequestBody GetPresignReqDto reqDto) {
        return appService.getPresignedUrl(reqDto);
    }


}
