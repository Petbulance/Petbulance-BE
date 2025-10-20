package com.example.Petbulance_BE.domain.user.entity;

import com.example.Petbulance_BE.domain.userEmail.UserEmails;
import com.example.Petbulance_BE.global.common.mapped.BaseTimeEntity;
import com.example.Petbulance_BE.global.common.type.Gender;
import com.example.Petbulance_BE.global.common.type.Role;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.Objects;

@Entity
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Users extends BaseTimeEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    private String nickname;

    private String profileImage;

    private String phoneNumber;

    private Boolean phoneNumberConnected;

    @Enumerated(EnumType.STRING)
    private Role role;

    @Builder.Default
    private Boolean suspended = false;

    @Enumerated(EnumType.STRING)
    private Gender gender;

    @Builder.Default
    private Boolean kakaoConnected = false;

    @Builder.Default
    private Boolean naverConnected = false;

    @Builder.Default
    private Boolean googleConnected = false;

    private LocalDate birth;

    @OneToOne(mappedBy = "user", cascade = CascadeType.REMOVE, orphanRemoval = true)
    private UserEmails userEmails;

    public void suspendUser() {
        this.suspended = true;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Users)) return false;
        Users user = (Users) o;
        return Objects.equals(id, user.id);
    }


}
