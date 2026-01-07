package com.example.Petbulance_BE.domain.admin.user.scheduling;

import com.example.Petbulance_BE.domain.user.entity.Users;
import com.example.Petbulance_BE.domain.user.repository.UsersJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

@Component
@RequiredArgsConstructor
public class UserCleanUpScheduler {

    private final UsersJpaRepository usersJpaRepository;

    @Scheduled(cron = "0 0 3 * * *") //초 분 시 일 월 요일 년
    @Transactional
    public void deleteExpiredUsers() {

        Duration duration = Duration.ofDays(30);

        LocalDateTime localDateTime = LocalDateTime.now().minusDays(duration.toDays());

        List<Users> deleteUsers = usersJpaRepository.findDeleteUsers(localDateTime);

        usersJpaRepository.deleteAll(deleteUsers);

    }

}
