package com.example.Petbulance_BE.domain.userEmail.entity;

import com.example.Petbulance_BE.domain.user.entity.Users;
import jakarta.persistence.*;
import lombok.*;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Builder
@Setter
public class UserEmails {

    @Id
    private String userId;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId
    @JoinColumn(name = "user_id")
    private Users user;

    @Setter
    @Column(unique = true)
    private String kakaoEmail;

    @Setter
    @Column(unique = true)
    private String naverEmail;

    @Setter
    @Column(unique = true)
    private String googleEmail;
}
