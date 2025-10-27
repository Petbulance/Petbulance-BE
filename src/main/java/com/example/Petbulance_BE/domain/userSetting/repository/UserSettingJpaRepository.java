package com.example.Petbulance_BE.domain.userSetting.repository;

import com.example.Petbulance_BE.domain.userSetting.entity.UserSetting;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserSettingJpaRepository extends JpaRepository<UserSetting, Long> {

}
