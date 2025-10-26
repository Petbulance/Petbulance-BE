package com.example.Petbulance_BE.domain.comment.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.domain.Slice;

import java.util.List;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class SearchPostCommentListResDto {
    private List<SearchPostCommentResDto> content;
    private boolean hasNext;
    private long totalCount;

    public SearchPostCommentListResDto(Slice<SearchPostCommentResDto> slice, long totalCount) {
        this.content = slice.getContent();
        this.hasNext = slice.hasNext();
        this.totalCount = totalCount;
    }
}
