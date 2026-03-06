package com.example.Petbulance_BE.domain.notification.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.domain.Slice;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PagingNotificationListResDto {
    private List<NotificationListResDto> content;
    private boolean hasNext;
    private Long lastNotificationId;

    public PagingNotificationListResDto(Slice<NotificationListResDto> slice) {
        this.content = slice.getContent();
        this.hasNext = slice.hasNext();

        if (!content.isEmpty()) {
            this.lastNotificationId = content.get(content.size() - 1).getNotificationId();
        }
    }
}
