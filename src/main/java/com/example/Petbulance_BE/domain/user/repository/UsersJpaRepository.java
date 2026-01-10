package com.example.Petbulance_BE.domain.user.repository;

import com.example.Petbulance_BE.domain.user.entity.Users;
import com.example.Petbulance_BE.global.common.type.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface UsersJpaRepository extends JpaRepository<Users, String>, UserRepositoryCustom {

    @Query("SELECT u FROM Users u WHERE u.id = :id")
    Optional<Users> findByIdForAuth(@Param("id") String id);

    @Query("SELECT u FROM Users u JOIN FETCH u.userEmails WHERE u.id = :id")
    Optional<Users> findByIdWithUserEmail(@Param("id") String id);

    @Query("SELECT u FROM Users u LEFT JOIN FETCH u.userSetting WHERE u.id = :id")
    Optional<Users> findByIdWithUserSetting(@Param("id") String id);

    Boolean existsByNickname(String nickname);

    Optional<Users> findByNickname(String nickname);

    @Query("SELECT u FROM Users u WHERE u.deleted = true AND u.updatedAt < :localDateTime")
    List<Users> findDeleteUsers(LocalDateTime localDateTime);


    List<Users> findByRole(Role role);
}
