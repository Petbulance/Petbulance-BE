package com.example.Petbulance_BE.domain.hospital.repository;

import com.example.Petbulance_BE.domain.hospital.entity.Hospital;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
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
            "WHERE h.id = :id")
    Optional<Hospital> findDetailHospital(@Param("id") Long id);

    @EntityGraph(attributePaths = {"hospitalWorktimes", "treatmentAnimals", "userReviews"})
    @Query("SELECT h FROM Hospital h WHERE h.id = :id")
    Optional<Hospital> findByIdWithDetails(@Param("id") Long id);

    @Query("""
        SELECT CAST(function('ST_Distance_Sphere', 
            function('ST_GeomFromText', CONCAT('POINT(', h.lat, ' ', h.lng, ')'),4326), 
            function('ST_GeomFromText', CONCAT('POINT(', :lat, ' ', :lng, ')'), 4326)
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

    //POINT(위도 경도)
    @Query(value = """
        SELECT *
        FROM hospitals
        WHERE MBRContains(
            ST_BUFFER(
                ST_PointFromText(CONCAT('POINT(', :lat, ' ', :lng, ')'), 4326),
                :radius
            ),
            hospitals.location
        )
        ORDER BY ST_Distance_Sphere(hospitals.location, ST_PointFromText(CONCAT('POINT(', :lat, ' ', :lng, ')'), 4326))
        LIMIT 1
        """, nativeQuery = true)
        List<Hospital> findNearestHospitals(@Param("lat") double lat, @Param("lng") double lng, @Param("radius") int radius);
    
    @Query("select h FROM Hospital  h WHERE h.name LIKE CONCAT(:hospitalName, '%')")
    List<Hospital> findByNameStartsWith(String hospitalName);

    Page<Hospital> findByNameContaining(Pageable pageable, String name);

    Long deleteHospitalById(@Param("id") Long id);
}
