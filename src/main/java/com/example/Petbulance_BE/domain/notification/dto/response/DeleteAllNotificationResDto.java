package com.example.Petbulance_BE.domain.notification.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class DeleteAllNotificationResDto {
    private String message;
    private int deletedCount;

    public static DeleteAllNotificationResDto of(int count) {
        return DeleteAllNotificationResDto.builder()
                .message("모든 알림이 삭제되었습니다.")
                .deletedCount(count)
                .build();
    }
}
