package com.example.Petbulance_BE.domain.qna.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PagingQnaListResDto {
    private List<QnaListResDto> content;
    private boolean hasNext;
}
