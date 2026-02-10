package com.example.Petbulance_BE.domain.review.aop;

import com.example.Petbulance_BE.global.common.error.exception.CustomException;
import com.example.Petbulance_BE.global.common.error.exception.ErrorCode;
import com.example.Petbulance_BE.global.util.UserUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.TimeUnit;

@Aspect
@Component
@RequiredArgsConstructor
@Slf4j
public class DailyLimitAspect {

    private final StringRedisTemplate redisTemplate;
    private final UserUtil userUtil;

    @Before("@annotation(dailyLimit)")
    public void checkLimit(DailyLimit dailyLimit) {
        String userId = userUtil.getCurrentUser().getId();
        String today = LocalDate.now().format(DateTimeFormatter.BASIC_ISO_DATE);

        //하루동안 보낸 요청수, 마지막 요청 시간 키
        String countKey = "daily_limit:"+userId+":"+today;
        String timeKey = "last_access:"+userId;

        //마지막 요청 시간
        String lastTimeStr = redisTemplate.opsForValue().get(timeKey);

        if(lastTimeStr != null) {
            long lastTime = Long.parseLong(lastTimeStr);
            long currentTime = System.currentTimeMillis();

            if (currentTime - lastTime < (dailyLimit.minInterval() * 1000L)){
                log.warn("너무 빠른 리뷰 요청 (매크로 의심)-UserId:{}",userId);
                throw new CustomException(ErrorCode.TOO_FAST_REQUEST);
            }
        }

        //ttl은 5초(3초 간격 검증이미로 충분)
        redisTemplate.opsForValue().set(timeKey, String.valueOf(System.currentTimeMillis()), 5, TimeUnit.SECONDS);

        Long count = redisTemplate.opsForValue().increment(countKey);

        if(count !=null && count == 1){
            redisTemplate.expire(countKey, 1, TimeUnit.DAYS);
        }

        if(count != null && count > dailyLimit.maxCount()){
            throw new CustomException(ErrorCode.TOO_MANY_REQUEST);
        }

    }



}
