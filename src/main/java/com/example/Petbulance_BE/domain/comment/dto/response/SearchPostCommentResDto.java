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
        this.commentId = commentId;
        this.boardId = boardId;
        this.boardName = boardName;
        this.postId = postId;
        this.postTitle = postTitle;
        this.writerNickname = writerNickname;
        this.commentContent = commentContent;
        this.createdAt = TimeUtil.formatCreatedAt(createdAt);
    }

}
