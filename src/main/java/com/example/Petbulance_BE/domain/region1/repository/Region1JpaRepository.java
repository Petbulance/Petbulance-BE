package com.example.Petbulance_BE.domain.region1.repository;

import com.example.Petbulance_BE.domain.region1.entity.Region1;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface Region1JpaRepository extends JpaRepository<Region1, Long> {

    @Query("SELECT r.name FROM Region1 r")
    List<String> findAllNames();

}
