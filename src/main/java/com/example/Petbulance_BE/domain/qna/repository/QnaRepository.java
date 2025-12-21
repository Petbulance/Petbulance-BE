package com.example.Petbulance_BE.domain.qna.repository;

import com.example.Petbulance_BE.domain.qna.dto.response.AdminQnaListResDto;
import com.example.Petbulance_BE.domain.qna.entity.Qna;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface QnaRepository extends JpaRepository<Qna, Long>, QnaRepositoryCustom {

}
