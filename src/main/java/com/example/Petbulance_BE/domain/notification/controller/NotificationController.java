package com.example.Petbulance_BE.domain.notification.controller;

import com.example.Petbulance_BE.domain.notification.dto.response.PagingNotificationListResDto;
import com.example.Petbulance_BE.domain.notification.service.NotificationService;
import com.example.Petbulance_BE.domain.post.dto.response.PagingPostListResDto;
import com.example.Petbulance_BE.domain.post.type.Topic;
import com.example.Petbulance_BE.global.common.type.AnimalType;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

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

}
