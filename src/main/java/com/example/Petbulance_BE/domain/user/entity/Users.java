package com.example.Petbulance_BE.domain.user.entity;

import com.example.Petbulance_BE.domain.device.entity.Device;
import com.example.Petbulance_BE.domain.recent.entity.History;
import com.example.Petbulance_BE.domain.userEmail.entity.UserEmails;
import com.example.Petbulance_BE.domain.userSetting.entity.UserSetting;
import com.example.Petbulance_BE.global.common.mapped.BaseTimeEntity;
import com.example.Petbulance_BE.global.common.type.Gender;
import com.example.Petbulance_BE.global.common.type.Role;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.util.List;
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

    @OneToMany(mappedBy = "user", cascade = CascadeType.REMOVE, orphanRemoval = true)
    private List<UserEmails> userEmails;

    public void suspendUser() {
        this.suspended = true;
    }
    @Setter
    @OneToMany(mappedBy = "user", cascade = CascadeType.REMOVE, orphanRemoval = true)
    private List<Device> devices;

    @OneToMany(mappedBy = "user", cascade = CascadeType.REMOVE, orphanRemoval = true)
    private List<UserSetting> userSetting;

    @OneToMany(mappedBy = "user", cascade = CascadeType.REMOVE, orphanRemoval = true)
    private List<History> histories;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Users)) return false;
        Users user = (Users) o;
        return Objects.equals(id, user.id);
    }


}
