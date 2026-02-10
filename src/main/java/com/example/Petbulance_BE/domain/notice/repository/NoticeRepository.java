package com.example.Petbulance_BE.domain.notice.repository;

import com.example.Petbulance_BE.domain.notice.entity.Notice;
import com.example.Petbulance_BE.domain.notice.type.PostStatus;
import com.example.Petbulance_BE.domain.user.entity.Users;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.Optional;

public interface NoticeRepository extends JpaRepository<Notice, Long>, NoticeRepositoryCustom {
     @Query("SELECT DISTINCT n FROM Notice n " +
             "LEFT JOIN FETCH n.buttons " + // 하나만 Fetch Join 유지
             "WHERE n.id = :noticeId")
     Optional<Notice> findWithButtons(@Param("noticeId") Long noticeId);
}
