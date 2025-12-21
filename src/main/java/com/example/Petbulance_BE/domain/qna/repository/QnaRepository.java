package com.example.Petbulance_BE.domain.qna.repository;

import com.example.Petbulance_BE.domain.qna.dto.response.AdminQnaListResDto;
import com.example.Petbulance_BE.domain.qna.entity.Qna;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface QnaRepository extends JpaRepository<Qna, Long>, QnaRepositoryCustom {

    @Query(
            value = """
            SELECT
                q.qna_id        AS qnaId,
                q.status        AS status,
                q.title         AS title,
                u.nickname      AS writerNickname,
                q.created_at    AS createdAt
            FROM qna q
            JOIN users u ON q.user_id = u.user_id
            WHERE (:lastQnaId IS NULL OR q.qna_id < :lastQnaId)
            ORDER BY q.qna_id DESC
            LIMIT :limit
        """,
            nativeQuery = true
    )
    List<AdminQnaListResDto> findAdminQnaList(
            @Param("lastQnaId") Long lastQnaId,
            @Param("limit") int limit
    );
}
