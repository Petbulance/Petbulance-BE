package com.example.Petbulance_BE.domain.review.dto;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.util.List;

public class GeminiApiDto {

    public static final String RECEIPT_EXTRACTION_PROMPT = """
                당신은 영수증 분석 전문가입니다.
                1. 이 이미지가 '동물병원' 영수증인지 판단하세요.
                2. 동물병원 영수증이 아니면, `{"status": "fail", "message": "동물병원 영수증이 아닙니다."}` 를 반환하세요.
                3. 동물병원 영수증이 맞다면, 아래 6개 항목을 추출하여 JSON 형식으로 반환하세요.
                   - `{"status": "success", "data": {"storeName": "...", "totalAmount": ..., "address": "...", "paymentTime": "..."}}`
                   - storeName: 매장명 (String)
                   - items: 영수증에 포함된 개별 구매/진료 항목 리스트 (Array)
                       - name: 품목명 또는 진료명 (String, 괄호나 대괄호가 있다면 포함해서 전체 이름 추출)
                       - price: 해당 항목의 가격 (Integer, 숫자만, 콤마 제거)
                   - totalAmount: 총 결제 금액 (Integer, 숫자만)
                   - address: 지오코딩 API를 위한 '표준 도로명 주소'를 추출하세요.
                               1. 시/도, 시/군/구, 도로명, 건물번호까지만 포함하세요. (예: 경기도 성남시 분당구 내정로 58)
                               2. **[필수] '도로명'과 '건물번호(숫자)'는 절대 누락하지 마세요.**
                               3. 건물 이름, 상가 호수, 층수 등 상세 위치 정보는 제거하세요. (예: '위브더스테이트 2층 213호' 같은 정보는 삭제)
                               4. 만약 도로명 주소가 없다면 '동/읍/면/리 + 번지수'까지만 포함된 지번 주소를 추출하세요.
                   - addressType: 'address 필드값이 도로명 주소라면 ("road"), 지번 주소라면 ("parcel")을 출력하세요.'
                   - paymentTime: 결제 시간 ('YYYY-MM-DD HH:MM:SS' 형식. 날짜만 있으면 'YYYY-MM-DD')
                4. 동물병원 영수증이 맞지만, 위 5개 항목을 추출할 수 없다면 `{"status": "fail", "message": "데이터 추출에 실패하였습니다."}` 를 반환하세요.
                5. 다른 설명 없이 오직 요청한 JSON 형식의 텍스트만 반환하세요.
                """;

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
            List<Item> items,
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

    public record Item(
            String name,
            Integer price
    ) {}
}
