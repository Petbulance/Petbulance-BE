package com.example.Petbulance_BE.domain.userSetting.entity;

import com.example.Petbulance_BE.domain.user.entity.Users;
import com.example.Petbulance_BE.global.common.mapped.BaseTimeEntity;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "userSettings")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserSetting extends BaseTimeEntity {

    @Id
    private String id;

    @MapsId
    @ManyToOne
    @JoinColumn(name = "user_id")
    private Users user;

    private Boolean totalPush;

    private Boolean eventPush;

    private Boolean marketingPush;

}
