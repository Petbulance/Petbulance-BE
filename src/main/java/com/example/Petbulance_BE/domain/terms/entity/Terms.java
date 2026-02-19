package com.example.Petbulance_BE.domain.terms.entity;

import com.example.Petbulance_BE.domain.terms.enums.TermsType;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
public class Terms {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    private TermsType type; // SERVICE, PRIVACY, LOCATION, MARKETING

    @Column(columnDefinition = "TEXT", nullable = false)
    private String content;

    @Column(nullable = false, unique = true)
    private String version;

    @Column(nullable = false)
    private Boolean isRequired; // 필수 여부

    private Boolean isActive;

    private LocalDateTime effectiveDate;

    private LocalDateTime cancellationDate;

}
