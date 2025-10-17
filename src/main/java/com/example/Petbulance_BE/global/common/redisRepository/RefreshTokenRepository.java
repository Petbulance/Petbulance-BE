package com.example.Petbulance_BE.global.common.redisRepository;

import com.example.Petbulance_BE.global.common.redisEntity.RefreshEntity;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RefreshTokenRepository extends CrudRepository<RefreshEntity, String> {

    Optional<RefreshEntity> findByUserId(String userId);
}
