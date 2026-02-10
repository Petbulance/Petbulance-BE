package com.example.Petbulance_BE.domain.board.repository;

import com.example.Petbulance_BE.domain.board.entity.Board;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface BoardRepository extends JpaRepository<Board, Long> {

    @Query("SELECT b.nameKr FROM Board b")
    List<String> findAllNameKr();
}
