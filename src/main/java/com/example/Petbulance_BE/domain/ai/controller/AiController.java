package com.example.Petbulance_BE.domain.ai.controller;

import com.example.Petbulance_BE.domain.ai.dto.res.DiagnosisResDto;
import com.example.Petbulance_BE.domain.ai.service.AiService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import reactor.core.publisher.Mono;

import java.util.List;

@RestController
@RequestMapping("/ai")
@Slf4j
@RequiredArgsConstructor
public class AiController {

    private final AiService aiService;

    @PostMapping("/diagnosis")
    public Mono<DiagnosisResDto> aiDiagnosis(@RequestParam(name = "images") List<MultipartFile> images,
    @RequestParam(name = "animalType", required = false)String animalType, @RequestParam(name = "symptom", required = false)String symptom) {
        return aiService.aiDiagnosisProcess(images, animalType, symptom);
    }

}
