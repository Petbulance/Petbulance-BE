package com.example.Petbulance_BE.domain.comment.repository;

import com.example.Petbulance_BE.domain.comment.entity.PostCommentCount;
import com.example.Petbulance_BE.domain.post.entity.Post;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface PostCommentCountRepository extends JpaRepository<PostCommentCount, Long> {
    // 좋아요 수 증가
    @Query(value = "UPDATE post_comment_count SET post_comment_count = post_comment_count + 1 WHERE post_id = :postId", nativeQuery = true)
    @Modifying(clearAutomatically = true)
    int increase(@Param("postId") Long postId);

    // 좋아요 수 감소
    @Query(value = "UPDATE post_comment_count SET post_comment_count = post_comment_count - 1 WHERE post_id = :postId", nativeQuery = true)
    @Modifying(clearAutomatically = true)
    int decrease(@Param("postId") Long postId);

    Optional<PostCommentCount> findByPostId(Long postId);
}
