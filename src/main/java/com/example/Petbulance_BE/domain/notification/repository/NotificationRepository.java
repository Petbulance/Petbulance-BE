package com.example.Petbulance_BE.domain.notification.repository;

import com.example.Petbulance_BE.domain.notification.entity.Notification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;

public interface NotificationRepository extends JpaRepository<Notification, Long>, NotificationRepositoryCustom {
    @Modifying(clearAutomatically = true)
    @Query("UPDATE Notification n SET n.read = true, n.readAt = :now " +
            "WHERE n.receiver.id = :receiverId " +
            "AND n.targetId = :postId " +
            "AND n.targetType IN (com.example.Petbulance_BE.domain.notification.type.NotificationTargetType.POST, " +
            "com.example.Petbulance_BE.domain.notification.type.NotificationTargetType.COMMENT, " +
            "com.example.Petbulance_BE.domain.notification.type.NotificationTargetType.SANCTION) " +
            "AND n.read = false")
    void markAsReadByPostId(@Param("receiverId") String receiverId,
                            @Param("postId") Long postId,
                            @Param("now") LocalDateTime now);
}
