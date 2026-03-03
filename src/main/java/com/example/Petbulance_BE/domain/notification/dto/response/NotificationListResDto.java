package com.example.Petbulance_BE.domain.notification.dto.response;

import com.example.Petbulance_BE.domain.post.type.Topic;
import com.example.Petbulance_BE.global.common.type.AnimalType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class NotificationListResDto {
    private Long notificationId;
    private AnimalType type;
    private Topic topic;
    private String createdAt;
    private String message;
    private boolean read;
}
