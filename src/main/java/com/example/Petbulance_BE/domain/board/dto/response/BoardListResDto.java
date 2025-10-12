package com.example.Petbulance_BE.domain.board.dto.response;

import com.example.Petbulance_BE.domain.board.entity.Board;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class BoardListResDto {
    private Long boardId;
    private String nameKr;
    private String nameEn;
    private String description;

    public static BoardListResDto of(Board board) {
        BoardListResDto dto = new BoardListResDto();
        dto.boardId = board.getId();
        dto.nameKr = board.getNameKr();
        dto.nameEn = board.getNameEn();
        dto.description = board.getDescription();
        return dto;
    }
}
