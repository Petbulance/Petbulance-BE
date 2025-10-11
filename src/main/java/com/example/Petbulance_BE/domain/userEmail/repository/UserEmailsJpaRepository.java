package com.example.Petbulance_BE.domain.userEmail.repository;

import com.example.Petbulance_BE.domain.user.entity.Users;
import com.example.Petbulance_BE.domain.userEmail.UserEmails;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface UserEmailsJpaRepository extends JpaRepository<UserEmails, String> {

    public Boolean existsByKakaoEmail(String email);

    public Boolean existsByNaverEmail(String email);

    public Boolean existsByGoogleEmail(String email);

    @Query("SELECT ue.user FROM UserEmails ue WHERE ue.kakaoEmail = :email")
    Optional<Users> findByKakaoEmail(@Param("email") String email);

    @Query("SELECT ue.user FROM UserEmails ue WHERE ue.naverEmail = :email")
    Optional<Users> findByNaverEmail(@Param("email") String email);

    @Query("SELECT ue.user FROM UserEmails ue WHERE ue.googleEmail = :email")
    Optional<Users> findByGoogleEmail(@Param("email") String email);
}
