package com.example.Petbulance_BE.domain.region2.repository;

import com.example.Petbulance_BE.domain.region2.entity.Region2;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface Region2JpaRepository extends JpaRepository<Region2, Long> {

    @Query("SELECT r.name FROM Region2 r")
    List<String> findAllNames();

}
