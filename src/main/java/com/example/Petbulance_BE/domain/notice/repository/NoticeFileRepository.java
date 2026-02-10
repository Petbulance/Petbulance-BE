package com.example.Petbulance_BE.domain.notice.repository;

import com.example.Petbulance_BE.domain.notice.entity.Notice;
import com.example.Petbulance_BE.domain.notice.entity.NoticeFile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface NoticeFileRepository extends JpaRepository<NoticeFile, Long> {
    List<NoticeFile> findAllByNoticeId(Long noticeId);

    @Query("SELECT n FROM Notice n " +
            "LEFT JOIN FETCH n.buttons " +
            "LEFT JOIN FETCH n.files " +
            "WHERE n.id = :noticeId")
    Optional<Notice> findWithButtonsAndFiles(@Param("noticeId") Long noticeId);
}
