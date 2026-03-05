package com.example.Petbulance_BE.domain.notification.service;

import com.example.Petbulance_BE.domain.notification.dto.response.DeleteAllNotificationResDto;
import com.example.Petbulance_BE.domain.notification.dto.response.NotificationListResDto;
import com.example.Petbulance_BE.domain.notification.dto.response.PagingNotificationListResDto;
import com.example.Petbulance_BE.domain.notification.dto.response.ReadAllNotificationResDto;
import com.example.Petbulance_BE.domain.notification.entity.Notification;
import com.example.Petbulance_BE.domain.notification.repository.NotificationRepository;
import com.example.Petbulance_BE.domain.notification.type.NotificationTargetType;
import com.example.Petbulance_BE.domain.notification.type.NotificationType;
import com.example.Petbulance_BE.domain.user.entity.Users;
import com.example.Petbulance_BE.global.util.UserUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class NotificationService {
    private final NotificationRepository notificationRepository;

    @Transactional(readOnly = true)
    public PagingNotificationListResDto notificationList(Long lastNotificationId, Integer pageSize) {
        Users currentUser = UserUtil.getCurrentUser();
        Slice<NotificationListResDto> resDtos = notificationRepository.fetchMyNotificationSlice(currentUser.getId(), lastNotificationId, pageSize);

        return new PagingNotificationListResDto(resDtos);
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

    @Transactional
    public ReadAllNotificationResDto readAllNotification() {
        Users currentUser = UserUtil.getCurrentUser();

        // 현재 시간 저장
        LocalDateTime now = LocalDateTime.now();

        // 벌크 업데이트 실행 (업데이트된 행의 개수를 반환함)
        int updatedCount = notificationRepository.markAllAsReadByReceiver(currentUser, now);
        return ReadAllNotificationResDto.of(updatedCount);
    }

    @Transactional
    public DeleteAllNotificationResDto deleteAllNotification() {
        Users currentUser = UserUtil.getCurrentUser();

        int count = notificationRepository.deleteAllByReceiver(currentUser);

        return DeleteAllNotificationResDto.of(count);
    }
}
