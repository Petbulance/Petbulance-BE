package com.example.Petbulance_BE.domain.admin.hospital.repository;

import com.example.Petbulance_BE.domain.admin.hospital.entity.HospitalHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface HospitalsHistoryJpaRepository extends JpaRepository<HospitalHistory, Long> {

    List<HospitalHistory> findByHospitalId(Long id);
}
