package com.example.Petbulance_BE.domain.recent.repository;

import com.example.Petbulance_BE.domain.recent.entity.History;
import com.example.Petbulance_BE.domain.recent.type.SearchType;
import com.example.Petbulance_BE.domain.user.entity.Users;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface HistoryJpaRepository extends JpaRepository<History, Long> {

    List<History> findTop5ByUserAndSearchTypeOrderByCreatedAtDesc(Users user, SearchType type);

    void deleteByIdAndUser(Long id, Users user);

    boolean existsByIdAndUser(Long id, Users user);

    Optional<History> findByUserAndSearchTypeAndContent(Users currentUser, SearchType searchType, String hospitalName);

    Optional<History> findByUserAndSearchTypeAndHospitalId(Users currentUser, SearchType searchType, Long hospitalId);

    Optional<History> findFirstByUserAndSearchTypeAndHospitalIdOrderByCreatedAtDesc(Users currentUser, SearchType searchType, Long hospitalId);
}
