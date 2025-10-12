package com.example.Petbulance_BE.domain.board.repository;

import com.example.Petbulance_BE.domain.board.entity.Board;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BoardRepository extends JpaRepository<Board, Long> {
}
