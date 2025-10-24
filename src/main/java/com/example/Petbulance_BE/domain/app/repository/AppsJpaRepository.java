package com.example.Petbulance_BE.domain.app.repository;

import com.example.Petbulance_BE.domain.app.entity.App;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface AppsJpaRepository extends JpaRepository<App, Long> {

    Optional<App> findTopByOrderByCreatedAtDesc();

}
