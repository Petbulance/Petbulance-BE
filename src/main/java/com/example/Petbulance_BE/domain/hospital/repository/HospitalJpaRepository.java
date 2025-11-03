package com.example.Petbulance_BE.domain.hospital.repository;

import com.example.Petbulance_BE.domain.hospital.dto.HospitalCardSelectDto;
import com.example.Petbulance_BE.domain.hospital.dto.HospitalSearchReqDto;
import com.example.Petbulance_BE.domain.hospital.dto.HospitalsResDto;
import com.example.Petbulance_BE.domain.hospital.entity.Hospital;
import com.example.Petbulance_BE.domain.review.entity.UserReview;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface HospitalJpaRepository extends JpaRepository<Hospital, Long>, HospitalRepositoryCustom {

    @Query("SELECT DISTINCT h FROM Hospital h " +
            "LEFT JOIN FETCH h.hospitalWorktimes hw " +
            "LEFT JOIN FETCH h.treatmentAnimals ta " +
            "WHERE h.id = :id" )
    Optional<Hospital> findDetailHospital(@Param("id") Long id);

    @EntityGraph(attributePaths = {"hospitalWorktimes", "treatmentAnimals", "userReviews"})
    @Query("SELECT h FROM Hospital h WHERE h.id = :id")
    Optional<Hospital> findByIdWithDetails(@Param("id") Long id);

    @Query("""
        SELECT CAST(function('ST_Distance_Sphere', 
            function('ST_GeomFromText', CONCAT('POINT(', h.lng, ' ', h.lat, ')'),4326), 
            function('ST_GeomFromText', CONCAT('POINT(', :lng, ' ', :lat, ')'), 4326)
        ) AS Double)
        FROM Hospital h
        WHERE h.id = :id
    """)
    Optional<Double> calculateDistance(
            @Param("lng") double lng,
            @Param("lat") double lat,
            @Param("id") Long id
    );

    @Query("SELECT AVG(r.overallRating) FROM UserReview r WHERE r.hospital.id = :id")
    Optional<Double> getOverallRating(@Param("id") Long id);
}
