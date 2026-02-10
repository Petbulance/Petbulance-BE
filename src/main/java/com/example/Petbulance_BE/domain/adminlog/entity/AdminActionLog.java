package com.example.Petbulance_BE.domain.adminlog.entity;

import com.example.Petbulance_BE.domain.adminlog.type.*;
import com.example.Petbulance_BE.domain.user.entity.Users;
import com.example.Petbulance_BE.global.common.mapped.BaseTimeEntity;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;
import org.hibernate.annotations.CreationTimestamp;
import org.springframework.data.annotation.CreatedDate;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.time.LocalDateTime;

@Entity
@Table(name = "admin_action_log")
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Setter
public class AdminActionLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "admin_action_log_id")
    private Long id;


    @Enumerated(EnumType.STRING)
    @Column(name = "actor_type", nullable = false, length = 20)
    private AdminActorType actorType; // ADMIN / SYSTEM

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "adamin_id")
    @Builder.Default
    private Users admin = null; // SYSTEM인 경우 null

    @Enumerated(EnumType.STRING)
    @Column(name = "page_type", nullable = false, length = 30)
    private AdminPageType pageType;

    @Enumerated(EnumType.STRING)
    @Column(name = "action_type", nullable = false, length = 50)
    private AdminActionType actionType;

    @Enumerated(EnumType.STRING)
    @Column(name = "target_type", length = 30)
    private AdminTargetType targetType;

    @Column(name = "target_id")
    @Builder.Default
    private String targetId = null; // 대상 엔티티 ID

    @Enumerated(EnumType.STRING)
    @Column(name = "result_type", nullable = false, length = 20)
    @Builder.Default
    private AdminActionResult resultType = AdminActionResult.FAIL;

    @Column(name = "description", nullable = false, columnDefinition = "TEXT")
    private String description;

    @CreationTimestamp
    private LocalDateTime createdAt;

}
