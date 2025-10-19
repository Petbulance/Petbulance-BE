package com.example.Petbulance_BE.domain.comment.dto.response;

import com.example.Petbulance_BE.global.util.TimeUtil;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class SearchPostCommentResDto {
    private Long commentId;
    private Long boardId;
    private String boardName;
    private Long postId;
    private String postTitle;
    private String writerNickname;
    private String commentContent;
    private String createdAt;

    public SearchPostCommentResDto(Long commentId, Long boardId, String boardName, Long postId, String postTitle, String writerNickname, String commentContent, LocalDateTime createdAt) {
        SearchPostCommentResDto dto = new SearchPostCommentResDto();
        dto.commentId = commentId;
        dto.boardId = boardId;
        dto.boardName = boardName;
        dto.postId = postId;
        dto.postTitle = postTitle;
        dto.writerNickname = writerNickname;
        dto.commentContent = commentContent;
        dto.createdAt = TimeUtil.formatCreatedAt(createdAt);
    }
}
