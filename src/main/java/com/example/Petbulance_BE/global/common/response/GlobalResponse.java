package com.example.Petbulance_BE.global.common.response;

import com.example.Petbulance_BE.global.common.error.ErrorResponse;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.Map;

@Getter
@Setter
@AllArgsConstructor
@Builder
public class GlobalResponse<T> {
    private int status;
    private boolean success;
    private T data;

    public static GlobalResponse success(int status, Object data) {
        return GlobalResponse.builder()
                .success(true)
                .status(status)
                .data(data)
                .build();
    }

    public static GlobalResponse failure(int status, ErrorResponse errorResponse) {
        return GlobalResponse.builder()
                .success(false)
                .status(status)
                .data(errorResponse)
                .build();
    }

    public static GlobalResponse failure2(int status, Map<String, String> map) {
        return GlobalResponse.builder()
                .success(false)
                .status(status)
                .data(map)
                .build();
    }
}