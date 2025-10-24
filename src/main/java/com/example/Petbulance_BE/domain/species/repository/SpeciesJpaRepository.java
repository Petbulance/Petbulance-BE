package com.example.Petbulance_BE.domain.species.repository;

import com.example.Petbulance_BE.domain.species.entity.Species;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SpeciesJpaRepository extends JpaRepository<Species,Long> {

    @Query("SELECT s.type FROM Species s")
    List<String> findAllTypes();
}
