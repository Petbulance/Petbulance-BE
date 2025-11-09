package com.example.Petbulance_BE.domain.comment.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.domain.Slice;

import java.util.List;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PagingPostCommentListResDto {
    private List<PostCommentListResDto> content;
    private boolean hasNext;
    private Long totalCommentCount;

    public PagingPostCommentListResDto(Slice<PostCommentListResDto> slice, Long totalCommentCount) {
        this.content = slice.getContent();
        this.hasNext = slice.hasNext();
        this.totalCommentCount = totalCommentCount;
    }
}
