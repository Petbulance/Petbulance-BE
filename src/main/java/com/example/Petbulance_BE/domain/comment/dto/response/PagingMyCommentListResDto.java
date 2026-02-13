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
    private Long lastCommentId;
    private boolean hasNext;

    public PagingMyCommentListResDto(List<MyCommentListResDto> content, boolean hasNext) {
        this.content = content;
        this.hasNext = hasNext;

        if (content != null && !content.isEmpty()) {
            this.lastCommentId = content.get(content.size() - 1).getCommentId();
        } else {
            this.lastCommentId = null;
        }
    }
}
