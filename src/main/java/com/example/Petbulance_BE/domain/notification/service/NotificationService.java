package com.example.Petbulance_BE.domain.notification.service;

import com.example.Petbulance_BE.domain.notification.dto.response.PagingNotificationListResDto;
import com.example.Petbulance_BE.domain.notification.entity.Notification;
import com.example.Petbulance_BE.domain.notification.repository.NotificationRepository;
import com.example.Petbulance_BE.domain.notification.type.NotificationTargetType;
import com.example.Petbulance_BE.domain.notification.type.NotificationType;
import com.example.Petbulance_BE.domain.user.entity.Users;
import com.example.Petbulance_BE.global.util.UserUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class NotificationService {
    private final NotificationRepository notificationRepository;

    @Transactional(readOnly = true)
    public PagingNotificationListResDto notificationList(Long lastNotificationId, Integer pageSize) {
        Users currentUser = UserUtil.getCurrentUser();
        notificationRepository.fetchMyNotificationSlice(currentUser.getId(), lastNotificationId, pageSize);
        return null;
    }

    @Transactional
    public void createNotification(Users receiver, Users actor, NotificationType type, NotificationTargetType targetType, Long targetId, String message) {
        notificationRepository.save(Notification.builder()
                .receiver(receiver)
                .actor(actor)
                .type(type)
                .targetType(targetType)
                .targetId(targetId)
                .message(message)
                .build());
    }
}
