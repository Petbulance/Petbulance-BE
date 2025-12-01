package com.example.Petbulance_BE.domain.ai.service;

import com.example.Petbulance_BE.domain.ai.dto.AiGeminiApiDto.*;
import com.example.Petbulance_BE.domain.ai.dto.res.DiagnosisResDto;
import com.example.Petbulance_BE.global.common.error.exception.CustomException;
import com.example.Petbulance_BE.global.common.error.exception.ErrorCode;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.util.retry.Retry;

import java.time.Duration;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;

@Service
@Slf4j
public class AiService {

    private final WebClient webClient;
    private final String urlWithKey;
    private final ObjectMapper objectMapper;
    private final String genimiApiUrl;

    public AiService(@Value("${rag.vertex.url-with-key}") String urlWithKey,
                     WebClient webClient,
                     ObjectMapper objectMapper,
                     @Value("${gemini.api.url-with-key}") String genimiApiUrl) {
        this.urlWithKey = urlWithKey;
        this.webClient = webClient;
        this.objectMapper = objectMapper;
        this.genimiApiUrl = genimiApiUrl;
    }

    // [재시도 전략 정의]: 에러 발생 시 2초 대기 후 1회 재시도 (최대 2번 실행됨)
    private final Retry retrySpec = Retry.backoff(1, Duration.ofSeconds(2))
            .filter(throwable -> {
                // 4xx 에러(Bad Request)는 재시도해도 똑같으므로 재시도 안 함
                if (throwable instanceof WebClientResponseException e) {
                    return e.getStatusCode().is5xxServerError();
                }
                // 그 외 네트워크 오류, 파싱 오류 등은 재시도
                return true;
            })
            .onRetryExhaustedThrow((retryBackoffSpec, retrySignal) -> {
                // 재시도까지 다 실패하면 발생하는 예외
                return new CustomException(ErrorCode.GEMINI_API_CONNECTION_ERROR);
            });


    public Mono<DiagnosisResDto> aiDiagnosisProcess(List<MultipartFile> images, String animalType, String symptom) {

        boolean hasImage = images != null && !images.isEmpty() && !images.get(0).isEmpty();

        Mono<List<ExtractedData>> imageAnalysisMono;

        if (hasImage) {
            log.info("이미지 발견: 1차 이미지 분석 수행");

            imageAnalysisMono = Flux.fromIterable(images)
                    .flatMap(image -> {
                        String base64;
                        try {
                            base64 = Base64.getEncoder().encodeToString(image.getBytes());
                        } catch (Exception e) {
                            return Mono.error(new CustomException(ErrorCode.IMAGE_PROCESSING_ERROR));
                        }

                        List<Part> parts = new ArrayList<>();
                        String prompt = String.format(
                                "당신은 동물 진단 전문가입니다. 아래 규칙을 따라 오직 JSON 형식으로 응답하세요. 입력 동물 타입 %s\n"
                                        + "1. 입력된 이미지가 동물이 아니면, {\"status\": \"fail\", \"message\": \"동물 이미지가 아닙니다.\"} 반환\n"
                                        + "2. 입력된 animalType과 이미지가 일치하지 않으면, {\"status\": \"fail\", \"message\": \"이미지와 입력된 동물 종이 일치하지 않습니다.\"} 반환\n"
                                        + "3. 이미지가 올바른 동물이고, animalType과 일치하면, {\"status\": \"success\", \"data\": {\"description\": \"...\", \"confidence\": ...}}\n"
                                        + "4. JSON 외 다른 텍스트는 절대 출력하지 마세요.", animalType
                        );
                        parts.add(new Part(prompt, null));
                        parts.add(new Part(null, new InlineData(image.getContentType(), base64)));

                        Content content = new Content(parts);
                        GeminiRequest geminiRequest = new GeminiRequest(List.of(content));

                        return webClient.post()
                                .uri(genimiApiUrl)
                                .bodyValue(geminiRequest)
                                .retrieve()
                                .bodyToMono(GeminiResponse.class)
                                .retryWhen(retrySpec)
                                .map(resp -> resp.candidates().get(0).content().parts().get(0).text()
                                        .replace("```json", "").replace("```", "").trim())
                                .flatMap(jsonString -> {
                                    try {
                                        GeminiJsonOutput output = objectMapper.readValue(jsonString, GeminiJsonOutput.class);
                                        if ("success".equals(output.status())) {
                                            return Mono.just(output.data());
                                        } else {
                                            return Mono.error(new CustomException(ErrorCode.BAD_IMAGE));
                                        }
                                    } catch (JsonProcessingException e) {
                                        return Mono.error(new RuntimeException("JSON 파싱 오류", e));
                                    }
                                })
                                .onErrorMap(e -> (e instanceof CustomException) ? e :
                                        new CustomException(ErrorCode.GEMINI_API_CONNECTION_ERROR));
                    })
                    .collectList(); // Mono<List<ExtractedData>>

        } else {
            log.info("이미지 없음: 텍스트 기반 진단으로 바로 진행");
            imageAnalysisMono = Mono.just(List.of(new ExtractedData(
                    "사용자가 이미지를 제공하지 않았습니다. 텍스트 증상에만 의존하여 진단하세요.", 0.0
            )));
        }

        // [2단계] RAG 진단 수행
        return imageAnalysisMono.flatMap(list -> {

            ExtractedData firstOutputData = list.get(0);

            String ragPrompt = String.format("""
                            당신은 수의학 응급 진단 전문가입니다. 애완동물인 만큼 사람처럼 지칭할 필요는 없어.
                            
                            [환자 정보]
                            - 동물 종: %s
                            - 보호자 호소 증상: %s
                            - 이미지 분석 결과: %s
                            
                            위 정보를 바탕으로 Vertex AI 검색 결과(RAG)를 참고하여 정밀 진단을 내려주세요.
                            반드시 무조건 절대로 아래 JSON 형식으로만 답변해야 합니다.
                            totalSteps는 항상 5로 고정이고 steps로 다섯가지 단계별 응급처치 방법이 존재해야합니다.
                            emergencyLevel은 "high", "middle", "low" 세가지 값중 하나만 지정될 수 있습니다.
                            
                            [필수 포함 항목]
                            - confidence: 검색된 정보와 증상의 일치도를 바탕으로 진단 신뢰도(0.0 ~ 1.0)를 계산하여 포함하세요.
                            
                            [JSON 응답 예시]
                            {
                              "status": "success",
                              "data": {
                                "emergencyLevel": "high",
                                "confidence": 0.85,
                                "detectedSymptoms": ["개구 호흡", "비공 거품"],
                                "suspectedDisease": "상부 호흡기 감염 의심",
                                "recommendedActions": ["2시간 이내 전문의 진료 권장", "강제 급식 금지", "이동 중 보온 유지 필수"],
                                "animalType": "%s",
                                "totalSteps": 5,
                                "steps": [
                                  { "description": "...", "warning": "..." },
                                  { "description": "...", "warning": "..." },
                                  { "description": "...", "warning": "..." },
                                  { "description": "...", "warning": "..." },
                                  { "description": "...", "warning": "..." }
                                ]
                              },
                              "message": null
                            }
                            """,
                    animalType, symptom, firstOutputData.description(), animalType);

            Content2 ragContent = new Content2("user", List.of(new Part(ragPrompt, null)));
            GeminiRequest2 geminiRequest2 = new GeminiRequest2(List.of(ragContent));

            // [2차 API 호출]
            return webClient.post()
                    .uri(urlWithKey)
                    .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                    .bodyValue(geminiRequest2)
                    .retrieve()
                    .bodyToMono(JsonNode.class)
                    .retryWhen(retrySpec) // 1회 재시도 적용 ⚡
                    .map(node -> {
                        try {
                            String rawText = node.path("candidates").get(0)
                                    .path("content").path("parts").get(0)
                                    .path("text").asText()
                                    .replace("```json", "")
                                    .replace("```", "")
                                    .trim();
                            log.info("RAG Raw Text: {}", rawText);
                            return rawText;
                        } catch (Exception e) {
                            // 구조가 다르면 재시도 (AI가 가끔 형식을 어길 때가 있음)
                            throw new RuntimeException("RAG 응답 구조 불일치", e);
                        }
                    })
                    .flatMap(finalJsonString -> {
                        try {
                            FinalResponseDto finalRes = objectMapper.readValue(finalJsonString, FinalResponseDto.class);
                            log.info("Final Parsed: {}", finalRes);

                            if (!"success".equals(finalRes.status())) {
                                log.error("RAG 진단 실패 메시지: {}", finalRes.message());
                                return Mono.error(new CustomException(ErrorCode.AI_DIAGNOSIS_FAIL));
                            }

                            DiagnosisResDto diagnosisResDto = mapToDiagnosisResDto(finalRes.data());
                            return Mono.just(diagnosisResDto);

                        } catch (Exception e) {
                            // JSON 파싱 에러 -> 재시도 대상
                            return Mono.error(new RuntimeException("최종 JSON 파싱 실패", e));
                        }
                    })
                    // 2차 과정에서 발생한 에러를 CustomException으로 매핑
                    .onErrorMap(e -> {
                        if (e instanceof CustomException) return e; // 이미 CustomException이면 통과
                        if (e instanceof JsonProcessingException || e.getMessage().contains("JSON")) {
                            return new CustomException(ErrorCode.AI_RESPONSE_PARSING_ERROR);
                        }
                        log.error("2차 진단 중 알 수 없는 에러", e);
                        return new CustomException(ErrorCode.AI_DIAGNOSIS_FAIL);
                    });
        });
    }

    private DiagnosisResDto mapToDiagnosisResDto(FinalData data) {
        return DiagnosisResDto.builder()
                .animalType(data.animalType())
                .emergencyLevel(data.emergencyLevel())
                .confidence(data.confidence())
                .detectedSymptoms(data.detectedSymptoms())
                .suspectedDisease(data.suspectedDisease())
                .recommendedActions(data.recommendedActions())
                .totalSteps(data.totalSteps())
                .steps(data.steps())
                .build();
    }
}