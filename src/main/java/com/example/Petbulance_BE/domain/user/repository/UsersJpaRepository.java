package com.example.Petbulance_BE.domain.user.repository;

import com.example.Petbulance_BE.domain.user.entity.Users;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UsersJpaRepository extends JpaRepository<Users, String> {

    public Boolean existsByNickname(String nickname);

}
