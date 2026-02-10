package com.example.Petbulance_BE.domain.comment.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PagingMyCommentListResDto {
    private List<MyCommentListResDto> content;
    private boolean hasNext;
}
