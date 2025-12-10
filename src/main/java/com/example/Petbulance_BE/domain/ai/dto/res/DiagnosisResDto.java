package com.example.Petbulance_BE.domain.ai.dto.res;

import com.example.Petbulance_BE.domain.ai.dto.AiGeminiApiDto;
import lombok.*;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DiagnosisResDto {

    private String animalType;

    private String emergencyLevel;

    private List<String> detectedSymptoms;

    private String suspectedDisease;

    private Integer totalSteps;

    private List<AiGeminiApiDto.Step> steps;

    private List<String> recommendedActions;

    private Double confidence;

}
