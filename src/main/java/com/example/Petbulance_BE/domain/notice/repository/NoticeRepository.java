package com.example.Petbulance_BE.domain.notice.repository;

import com.example.Petbulance_BE.domain.notice.entity.Notice;
import com.example.Petbulance_BE.domain.notice.type.PostStatus;
import com.example.Petbulance_BE.domain.user.entity.Users;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDate;

public interface NoticeRepository extends JpaRepository<Notice, Long>, NoticeRepositoryCustom {

    @Modifying(clearAutomatically = true)
    @Query("""
        UPDATE Notice n
           SET n.postStatus = :status
         WHERE n.postEndDate < :today
           AND n.postStatus <> :status
    """)
    int deactivateExpired(LocalDate today, PostStatus status);

}
