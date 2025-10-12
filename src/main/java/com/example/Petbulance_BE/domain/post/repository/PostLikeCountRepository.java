package com.example.Petbulance_BE.domain.post.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface PostLikeCountRepository extends JpaRepository<com.example.Petbulance_BE.domain.post.entity.PostLikeCount, Long> {

    // 좋아요 수 증가
    @Query(value = "UPDATE post_like_count SET post_like_count = post_like_count + 1 WHERE post_id = :postId", nativeQuery = true)
    @org.springframework.data.jpa.repository.Modifying(clearAutomatically = true)
    int increase(@Param("postId") Long postId);

    // 좋아요 수 감소
    @Query(value = "UPDATE post_like_count SET post_like_count = post_like_count - 1 WHERE post_id = :postId", nativeQuery = true)
    @org.springframework.data.jpa.repository.Modifying(clearAutomatically = true)
    int decrease(@Param("postId") Long postId);

    @Query(value = "SELECT p.post_like_count FROM post_like_count p WHERE p.post_id = :postId", nativeQuery = true)
    Long getCountByPostId(@Param("postId") Long postId);
}
