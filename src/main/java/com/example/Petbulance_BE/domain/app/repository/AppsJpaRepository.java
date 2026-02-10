package com.example.Petbulance_BE.domain.app.repository;

import com.example.Petbulance_BE.domain.app.dto.MetadataDto;
import com.example.Petbulance_BE.domain.app.dto.MetadataResponseDto;
import com.example.Petbulance_BE.domain.app.entity.App;
import com.example.Petbulance_BE.domain.app.type.VersionType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface AppsJpaRepository extends JpaRepository<App, Long> {

    Optional<App> findAppByVersionType(VersionType versionType);

    @Query("SELECT new com.example.Petbulance_BE.domain.app.dto.MetadataDto(" +
            " MAX(CASE WHEN a.versionType = 'REGION' THEN a.version END), " +
            " MAX(CASE WHEN a.versionType = 'SPECIES' THEN a.version END), " +
            " MAX(CASE WHEN a.versionType = 'COMMUNITY' THEN a.version END) " +
            ") FROM App a")
    MetadataDto findMetatDto ();

}
