package com.example.Petbulance_BE.domain.device.repository;

import com.example.Petbulance_BE.domain.device.entity.Device;
import com.example.Petbulance_BE.domain.user.entity.Users;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DeviceJpaRepository extends JpaRepository<Device, Long> {

    @Modifying
    @Query("DELETE FROM Device d WHERE d.fcm_token = :fcmToken AND d.user.id = :userId")
    void deleteByFcmTokenAndUserId(@Param("fcmToken") String fcmToken, @Param("userId") String userId);

    List<Device> findByUser(Users users);
}
