package com.example.Petbulance_BE.domain.notice.entity;

import com.example.Petbulance_BE.domain.user.entity.Users;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "buttons")
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Button {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "button_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private Notice notice;

    private String text;
    private String position;
    private String link;
    private String target;

}
