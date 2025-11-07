package com.example.Petbulance_BE.domain.post.repository;

import com.example.Petbulance_BE.domain.post.entity.PostViewCount;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

public interface PostViewCountBackUpRepository extends JpaRepository<PostViewCount, Long> {

    @Modifying
    @Transactional
    @Query(value = """
        INSERT INTO view_count (post_id, view_count, updated_at)
        VALUES (:postId, :count, NOW())
        ON DUPLICATE KEY UPDATE
            view_count = view_count + :count,
            updated_at = NOW()
    """, nativeQuery = true)
    void upsertViewCount(Long postId, Long count);
}
