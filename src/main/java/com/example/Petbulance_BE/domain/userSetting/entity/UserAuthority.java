package com.example.Petbulance_BE.domain.userSetting.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "user_authority")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserAuthority {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Builder.Default
    private Boolean locationService = false;

    @Builder.Default
    private Boolean marketing = false;

    @Builder.Default
    private Boolean camera = false;

}
