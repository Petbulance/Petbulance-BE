package com.example.Petbulance_BE.domain.hospitalWorktime.repository;

import com.example.Petbulance_BE.domain.hospitalWorktime.entity.HospitalWorktime;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface HospitalWorktimeJpaRepository extends JpaRepository<HospitalWorktime, Long> {
}
