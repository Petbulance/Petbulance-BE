package com.example.Petbulance_BE.domain.user.repository;

import com.example.Petbulance_BE.domain.user.entity.UserSanction;
import com.example.Petbulance_BE.domain.user.entity.Users;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface UserSanctionRepository extends JpaRepository<UserSanction, Long> {
    List<UserSanction> findByUserAndActiveIsTrue(Users user);
}
