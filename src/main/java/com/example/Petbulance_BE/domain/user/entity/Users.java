package com.example.Petbulance_BE.domain.user.entity;

import com.example.Petbulance_BE.domain.userEmail.UserEmails;
import com.example.Petbulance_BE.global.common.mapped.BaseTimeEntity;
import com.example.Petbulance_BE.global.common.type.Gender;
import com.example.Petbulance_BE.global.common.type.Role;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;

@Entity
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Users extends BaseTimeEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @Setter
    private String nickname;

    @Setter
    private String profileImage;

    private String phoneNumber;

    private Boolean phoneNumberConnected;

    @Enumerated(EnumType.STRING)
    private Role role;

    @Builder.Default
    private Boolean suspended = false;

    @Enumerated(EnumType.STRING)
    private Gender gender;

    @Setter
    @Builder.Default
    private Boolean kakaoConnected = false;

    @Setter
    @Builder.Default
    private Boolean naverConnected = false;

    @Setter
    @Builder.Default
    private Boolean googleConnected = false;

    private LocalDate birth;

    @OneToOne(mappedBy = "user", cascade = CascadeType.REMOVE, orphanRemoval = true)
    private UserEmails userEmails;

    public void suspendUser() {
        this.suspended = true;
    }
}
