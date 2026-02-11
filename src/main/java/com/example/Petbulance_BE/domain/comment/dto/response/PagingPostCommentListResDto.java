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
    private Long lastParentCommentId;
    private Long lastCommentId;
    private boolean hasNext;
    private Long totalCommentCount;

    public PagingPostCommentListResDto(Slice<PostCommentListResDto> slice, Long totalCommentCount) {
        this.content = slice.getContent();
        this.hasNext = slice.hasNext();
        this.totalCommentCount = totalCommentCount;

        if (this.content != null && !this.content.isEmpty()) {
            PostCommentListResDto last = this.content.get(this.content.size() - 1);
            this.lastParentCommentId = last.getParentId();
            this.lastCommentId = last.getCommentId();
        } else {
            this.lastParentCommentId = null;
            this.lastCommentId = null;
        }
    }
}
