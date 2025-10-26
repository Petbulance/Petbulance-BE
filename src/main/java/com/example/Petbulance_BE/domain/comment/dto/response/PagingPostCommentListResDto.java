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

    public PagingPostCommentListResDto(Slice<PostCommentListResDto> slice) {
        this.content = slice.getContent();
        this.hasNext = slice.hasNext();
    }
}
