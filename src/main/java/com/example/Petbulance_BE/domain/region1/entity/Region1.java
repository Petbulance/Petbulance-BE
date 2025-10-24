package com.example.Petbulance_BE.domain.region1.entity;

import com.example.Petbulance_BE.domain.region2.entity.Region2;
import com.example.Petbulance_BE.global.common.mapped.BaseTimeEntity;
import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "regions1")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class Region1 extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    private String code;

    @OneToMany(mappedBy = "region1", orphanRemoval = true, cascade = CascadeType.REMOVE)
    private List<Region2> region2 = new ArrayList<>();
}
