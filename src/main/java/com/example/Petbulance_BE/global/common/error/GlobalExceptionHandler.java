package com.example.Petbulance_BE.global.common.error;

import com.example.Petbulance_BE.domain.admin.user.exception.ReviewBannedException;
import com.example.Petbulance_BE.domain.report.exception.CommunityBannedException;
import com.example.Petbulance_BE.global.common.error.exception.CustomException;
import com.example.Petbulance_BE.global.common.error.exception.ErrorCode;
import com.example.Petbulance_BE.global.common.response.GlobalResponse;
import io.sentry.Sentry;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

@RestControllerAdvice
@RequiredArgsConstructor
public class GlobalExceptionHandler {

    @ExceptionHandler(CustomException.class)
    public ResponseEntity<GlobalResponse> handleCustomException(CustomException e) {
        Sentry.captureException(e);
        final ErrorCode errorCode = e.getErrorCode();
        final ErrorResponse errorResponse = ErrorResponse.of(errorCode.name(), e.getMessage());
        final GlobalResponse response = GlobalResponse.failure(errorCode.getStatus().value(), errorResponse);
        return ResponseEntity.status(errorCode.getStatus()).body(response);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<GlobalResponse> handleValidationException(MethodArgumentNotValidException e) {
        Sentry.captureException(e);
        Map<String, String> errors = new HashMap<>();
        for (FieldError fieldError : e.getBindingResult().getFieldErrors()) {
            errors.put(fieldError.getField(), fieldError.getDefaultMessage());
        }

        ErrorResponse errorResponse = ErrorResponse.of("VALIDATION_ERROR", errors.toString());
        GlobalResponse response = GlobalResponse.failure(HttpStatus.BAD_REQUEST.value(), errorResponse);
        return ResponseEntity.badRequest().body(response);
    }

    @ExceptionHandler(CommunityBannedException.class)
    public ResponseEntity<?> handleCommunityBanned(CommunityBannedException e) {
        return ResponseEntity
                .status(HttpStatus.FORBIDDEN)
                .body(Map.of(
                        "error", "COMMUNITY_BANNED",
                        "message", e.getMessage(),
                        "bannedUntil", e.getBannedUntil()
                ));
    }

    @ExceptionHandler(ReviewBannedException.class)
    public ResponseEntity<GlobalResponse> handleReviewBanned(ReviewBannedException e) {
        Map<String, String> map = new LinkedHashMap<>();
        map.put("error", "REVIEW_BANNED");
        map.put("bannedUntil", e.getBannedUntil().toString());
        map.put("message", e.getMessage());
        GlobalResponse response = GlobalResponse.failure2(HttpStatus.FORBIDDEN.value(), map);
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(response);
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<GlobalResponse> handleHttpMessageNotReadableException(HttpMessageNotReadableException e) {
        final ErrorResponse errorCode = ErrorResponse.of(ErrorCode.INVALID_JSON_FORMAT.name(), ErrorCode.INVALID_JSON_FORMAT.getMessage());
        final GlobalResponse response = GlobalResponse.failure(ErrorCode.INVALID_JSON_FORMAT.getStatus().value(), errorCode);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }

}

