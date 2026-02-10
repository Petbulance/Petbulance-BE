package com.example.Petbulance_BE.domain.ai.dto;

import com.example.Petbulance_BE.domain.review.dto.GeminiApiDto;
import com.fasterxml.jackson.annotation.JsonInclude;

import java.util.List;

public class AiGeminiApiDto {

    /**
     * Gemini 요청의 '부분' (텍스트 또는 이미지)
     * "text"나 "inline_data" 둘 중 하나만 값을 가짐
     */
    @JsonInclude(JsonInclude.Include.NON_NULL) // null 필드는 JSON 생성 시 제외
    public record Part(String text, InlineData inlineData) {}

    /**
     * 'Part'가 이미지일 경우, 이미지 데이터를 담는 상자
     * (mime_type: "image/jpeg", data: "Base64인코딩된데이터")
     */
    public record InlineData(String mime_type, String data) {}

    /**
     * Gemini 요청의 '내용' (여러 Part의 묶음)
     * (예: 텍스트 Part 1개 + 이미지 Part 1개)
     */
    public record Content(List<Part> parts) {}

    public record Content2(String role, List<Part> parts) {}

    /**
     * Gemini API 요청의 최종 JSON 본문 상자
     */
    public record GeminiRequest(List<Content> contents) {}

    public record GeminiRequest2(List<Content2> contents) {}
    /**
     * Gemini API의 응답 후보
     */

    public record GeminiResponse(List<Candidate> candidates) {}

    public record Candidate(Content content) {}

    //최종 추출 데이터
    public record FinalExtractedData(
            String emergencyLevel,
            List<String> detectedSymptoms,
            String suspectedDisease,
            List<String> recommendedActions,
            Integer totalSteps

    ) {}

    public record ExtractedData(
            String description,
            Double confidence,
            String animalType
    ) {}

    /**
     * Gemini가 생성한 JSON 전체를 파싱할 DTO
     */
    public record GeminiJsonOutput(
            String status,
            ExtractedData data, // status가 "fail"이면 null
            String message     // status가 "success"이면 null
    ) {}

    public record FinalData(
            String emergencyLevel,          // 응급도 (High/Medium/Low)
            Double confidence,              // [NEW] 진단 신뢰도 (0.0 ~ 1.0) 추가!
            List<String> detectedSymptoms,  // 감지된 증상
            String suspectedDisease,        // 의심 질환
            List<String> recommendedActions,// 권장 행동
            String animalType,              // 동물 종류
            Integer totalSteps,             // 조치 단계 수
            List<Step> steps                // 상세 조치 단계
    ) {}

    public record Step(
            String description,
            String warning
    ) {}

    public record FinalResponseDto(
            String status,
            FinalData data,
            String message
    ) {}

}
