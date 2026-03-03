package com.example.Petbulance_BE.domain.notification.dto.response;

import com.example.Petbulance_BE.domain.notification.type.NotificationTargetType;
import com.example.Petbulance_BE.domain.post.type.Topic;
import com.example.Petbulance_BE.global.common.type.AnimalType;
import com.example.Petbulance_BE.global.util.TimeUtil;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class NotificationListResDto {
    private Long notificationId;
    private String type;
    private String topic;
    private String createdAt;
    private String message;
    private boolean read;
    private NotificationTargetType targetType;
    private Long targetId;

    public NotificationListResDto(Long notificationId, AnimalType type, Topic topic, LocalDateTime createdAt, String message, boolean read, NotificationTargetType targetType, Long targetId) {
        this.notificationId = notificationId;
        this.type = type.getDescription();
        this.topic = topic.getDescription();
        this.createdAt = TimeUtil.formatCreatedAt(createdAt);
        this.message = message;
        this.read = read;
        this.targetType = targetType;
        this.targetId = targetId;
    }
}
