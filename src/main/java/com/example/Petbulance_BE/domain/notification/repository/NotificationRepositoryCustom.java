package com.example.Petbulance_BE.domain.notification.repository;

import com.example.Petbulance_BE.domain.notification.dto.response.NotificationListResDto;
import org.springframework.data.domain.Slice;

public interface NotificationRepositoryCustom {
    Slice<NotificationListResDto> fetchMyNotificationSlice(String receiverId, Long lastNotificationId, int size);
}
