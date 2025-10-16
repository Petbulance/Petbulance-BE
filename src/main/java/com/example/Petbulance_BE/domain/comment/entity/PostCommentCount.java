package com.example.Petbulance_BE.domain.comment.entity;

import com.example.Petbulance_BE.domain.post.entity.Post;
import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "post_comment_count")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PostCommentCount {

    @Id
    @Column(name = "post_id")
    private Long postId;


    @Column(name = "post_comment_count", nullable = false)
    private Long postCommentCount = 0L;

    @Column(name = "updated_at", nullable = false,
            columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP")
    private LocalDateTime updatedAt;

    @PrePersist
    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }
}

