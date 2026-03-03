package com.example.Petbulance_BE.domain.notification.repository;

import com.example.Petbulance_BE.domain.notification.entity.Notification;
import org.springframework.data.jpa.repository.JpaRepository;

public interface NotificationRepository extends JpaRepository<Notification, Long>, NotificationRepositoryCustom {
}
