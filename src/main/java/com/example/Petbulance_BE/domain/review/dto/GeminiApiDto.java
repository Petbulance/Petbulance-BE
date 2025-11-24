package com.example.Petbulance_BE.domain.review.dto;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.util.List;

public class GeminiApiDto {

// --- Request DTOs (Gemini API로 보낼 JSON 상자) ---

    /**
     * Gemini 요청의 '부분' (텍스트 또는 이미지)
     * "text"나 "inline_data" 둘 중 하나만 값을 가짐
     */
    @JsonInclude(JsonInclude.Include.NON_NULL) // null 필드는 JSON 생성 시 제외
    public record Part(String text, InlineData inline_data) {}

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

    /**
     * Gemini API 요청의 최종 JSON 본문 상자
     */
    public record GeminiRequest(List<Content> contents) {}


// --- Response DTOs (Gemini API가 보낸 JSON을 받을 상자) ---

    /**
     * Gemini API 응답의 최상위 상자
     * (우리는 'candidates' 필드만 필요함)
     */
    public record GeminiResponse(List<Candidate> candidates) {}

    /**
     * Gemini API의 '응답 후보' 상자
     * (우리는 'content' 필드만 필요함)
     */
    public record Candidate(Content content) {}

    /**
     * Gemini가 생성한 JSON의 "data" 부분을 받을 DTO
     */
    public record ExtractedData(
            String storeName,
            Long totalAmount,
            String address,
            String addressType,
            String paymentTime
    ) {}

    /**
     * Gemini가 생성한 JSON 전체를 파싱할 DTO
     */
    public record GeminiJsonOutput(
            String status,
            ExtractedData data, // status가 "fail"이면 null
            String message     // status가 "success"이면 null
    ) {}

}
