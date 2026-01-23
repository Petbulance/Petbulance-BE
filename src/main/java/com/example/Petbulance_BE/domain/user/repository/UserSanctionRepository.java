package com.example.Petbulance_BE.domain.user.repository;

import com.example.Petbulance_BE.domain.user.entity.UserSanction;
import com.example.Petbulance_BE.domain.user.entity.Users;
import com.example.Petbulance_BE.domain.user.type.SanctionType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface UserSanctionRepository extends JpaRepository<UserSanction, Long> {
    List<UserSanction> findByUserAndActiveIsTrue(Users user);
    List<UserSanction> findAllByUserAndActiveTrueAndSanctionType(Users user, SanctionType type);

    List<UserSanction> findByUserId(String userID);
}
