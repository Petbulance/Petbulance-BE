package com.example.Petbulance_BE.domain.hospital.repository;

import com.example.Petbulance_BE.domain.hospital.entity.Hospital;
import com.example.Petbulance_BE.domain.hospital.entity.Tag;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TagJpaRepository extends JpaRepository<Tag, Long> {
    void deleteByHospital(Hospital hospital);

    List<Tag> findByHospitalId(Long id);
}
