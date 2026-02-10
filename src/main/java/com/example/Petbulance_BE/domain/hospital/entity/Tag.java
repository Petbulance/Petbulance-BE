package com.example.Petbulance_BE.domain.hospital.entity;

import com.example.Petbulance_BE.domain.hospital.type.TagType;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Tag {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    private Hospital hospital;

    @Enumerated(EnumType.STRING)
    private TagType tagType;

    private String tag;

}
