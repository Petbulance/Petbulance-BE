package com.example.Petbulance_BE.domain.user.repository;

import com.example.Petbulance_BE.domain.user.entity.Users;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UsersJpaRepository extends JpaRepository<Users, String> {
    Optional<Users> findByNickname(String nickname);
}
