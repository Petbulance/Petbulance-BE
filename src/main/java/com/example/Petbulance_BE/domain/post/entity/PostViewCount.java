package com.example.Petbulance_BE.domain.post.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "post_view_count")
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PostViewCount {

    @Id
    private Long postId;

    private Long viewCount;

    private java.time.LocalDateTime updatedAt;
}
