package com.example.Petbulance_BE.global.common.redisEntity;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;
import org.springframework.data.redis.core.TimeToLive;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@RedisHash(value = "refresh")
public class RefreshEntity {
    @Id
    private String userId;

    private String refresh;

    @TimeToLive
    private Long expiration;
}
