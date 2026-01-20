package com.example.Petbulance_BE.domain.terms.repository;

import com.example.Petbulance_BE.domain.terms.enums.TermsType;
import com.example.Petbulance_BE.domain.terms.entity.Terms;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TermsJpaRepository extends JpaRepository<Terms, Long> {

    @Query("SELECT t FROM Terms t WHERE t.isActive=true")
    List<Terms> findAllActive();

    @Query("UPDATE Terms t SET t.isActive = false WHERE t.isActive = true and t.type = :type")
    @Modifying
    Integer updateUnActive(@Param("type") TermsType type);

    @Query("SELECT t FROM Terms t WHERE t.isActive = true AND t.type = :type")
    Optional<Terms> findOneActive(@Param("type") TermsType type);
}
