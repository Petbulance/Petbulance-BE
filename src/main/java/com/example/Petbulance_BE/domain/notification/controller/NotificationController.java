package com.example.Petbulance_BE.domain.notification.controller;

import com.example.Petbulance_BE.domain.notification.dto.response.DeleteAllNotificationResDto;
import com.example.Petbulance_BE.domain.notification.dto.response.PagingNotificationListResDto;
import com.example.Petbulance_BE.domain.notification.dto.response.ReadAllNotificationResDto;
import com.example.Petbulance_BE.domain.notification.service.NotificationService;
import com.example.Petbulance_BE.domain.post.dto.response.PagingPostListResDto;
import com.example.Petbulance_BE.domain.post.type.Topic;
import com.example.Petbulance_BE.global.common.type.AnimalType;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/notifications")
public class NotificationController {
    private final NotificationService notificationService;

    @GetMapping
    public PagingNotificationListResDto notificationList(@RequestParam(required = false) Long lastNotificationId,
                                                         @RequestParam(defaultValue = "20") Integer pageSize) {
        return notificationService.notificationList(lastNotificationId, pageSize);
    }

    @PatchMapping("/read-all")
    public ReadAllNotificationResDto readAllNotification() {
        return notificationService.readAllNotification();
    }

    @GetMapping
    public DeleteAllNotificationResDto deleteAllNotification() {
        return notificationService.deleteAllNotification();
    }

}
