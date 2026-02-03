package com.example.Petbulance_BE.domain.userSetting.repository;

import com.example.Petbulance_BE.domain.user.entity.Users;
import com.example.Petbulance_BE.domain.userSetting.entity.UserSetting;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserSettingJpaRepository extends JpaRepository<UserSetting, Long> {

    UserSetting findFirstByUser(Users currentUser);

}
