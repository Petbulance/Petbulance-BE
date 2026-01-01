package com.example.Petbulance_BE.domain.treatmentAnimal.repository;

import com.example.Petbulance_BE.domain.treatmentAnimal.entity.TreatmentAnimal;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TreatmentAnimalJpaRepository extends JpaRepository <TreatmentAnimal, Long> {
}
