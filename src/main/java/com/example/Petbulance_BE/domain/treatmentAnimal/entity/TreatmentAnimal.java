package com.example.Petbulance_BE.domain.treatmentAnimal.entity;

import com.example.Petbulance_BE.domain.hospital.entity.Hospital;
import com.example.Petbulance_BE.global.common.mapped.BaseTimeEntity;
import com.example.Petbulance_BE.global.common.type.AnimalType;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "treatment_animals")
public class TreatmentAnimal extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @JoinColumn(name = "hospital_id")
    @ManyToOne(fetch = FetchType.LAZY)
    Hospital hospital;

    @Enumerated(EnumType.STRING)
    private AnimalType animalType;

}
