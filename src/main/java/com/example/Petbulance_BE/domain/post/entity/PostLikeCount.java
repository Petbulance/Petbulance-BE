package com.example.Petbulance_BE.domain.post.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Builder
@Entity
@Table(name = "post_like_count")
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class PostLikeCount {

    @Id
    @Column(name = "post_id")
    private Long postId;

    @Column(name = "post_like_count", nullable = false)
    private Long postLikeCount;

    @Column(name = "updated_at", nullable = false, columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP")
    private LocalDateTime updatedAt;

    @PrePersist
    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }
}
