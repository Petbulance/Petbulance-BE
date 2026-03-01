package com.example.Petbulance_BE.domain.comment.dto.response;

import com.example.Petbulance_BE.domain.post.type.Topic;
import com.example.Petbulance_BE.global.common.type.AnimalType;
import com.example.Petbulance_BE.global.util.TimeUtil;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.cglib.core.Local;

import java.sql.Time;
import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class SearchPostCommentResDto {
    private Long commentId;
    private Long postId;
    private String postTitle;
    private String commentContent;
    private String commentImageUrl;
    private String writerNickname;
    private String createdAt;
    private AnimalType type;
    private Topic topic;

    public SearchPostCommentResDto(Long commentId, Long postId, String postTitle, String commentContent, String commentImageUrl, String writerNickname, LocalDateTime createdAt, AnimalType type, Topic topic) {
        this.commentId = commentId;
        this.postId = postId;
        this.postTitle = postTitle;
        this.commentContent = commentContent;
        this.commentImageUrl = commentImageUrl;
        this.writerNickname = writerNickname;
        this.createdAt = TimeUtil.formatCreatedAt(createdAt);
        this.type = type;
        this.topic = topic;
    }
}
