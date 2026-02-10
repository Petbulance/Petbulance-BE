package com.example.Petbulance_BE.domain.board.entity;

import jakarta.persistence.*;
import lombok.*;
@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
@Table(name = "boards")
public class Board {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "board_id")
    private Long id;

    @Column(name = "name_kr", nullable = false, length = 50)
    private String nameKr;

    @Column(name = "name_en", nullable = false, unique = true, length = 50)
    private String nameEn;

    @Column(name = "description", length = 255)
    private String description;

}
