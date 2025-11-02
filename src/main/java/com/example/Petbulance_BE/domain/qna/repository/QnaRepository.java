package com.example.Petbulance_BE.domain.qna.repository;

import com.example.Petbulance_BE.domain.qna.entity.Qna;
import org.springframework.data.jpa.repository.JpaRepository;

public interface QnaRepository extends JpaRepository<Qna, Long> {
}
