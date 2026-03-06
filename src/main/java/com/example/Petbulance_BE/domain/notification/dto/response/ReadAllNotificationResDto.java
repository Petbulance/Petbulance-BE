package com.example.Petbulance_BE.domain.notification.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ReadAllNotificationResDto {
    private String message;
    private int updatedCount;

    public static ReadAllNotificationResDto of(int count) {
        return ReadAllNotificationResDto.builder()
                .message("모든 알림이 읽음 처리되었습니다.")
                .updatedCount(count)
                .build();
    }
}
