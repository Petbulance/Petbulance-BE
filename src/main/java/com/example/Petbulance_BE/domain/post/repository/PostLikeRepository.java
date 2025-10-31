package com.example.Petbulance_BE.domain.post.repository;

import com.example.Petbulance_BE.domain.post.entity.Post;
import com.example.Petbulance_BE.domain.post.entity.PostLike;
import com.example.Petbulance_BE.domain.user.entity.Users;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;
import java.util.Set;

public interface PostLikeRepository extends JpaRepository<PostLike, Long> {
    boolean existsByPostIdAndUser(Long postId, Users currentUser);
    Optional<PostLike> findByPostAndUser(Post post, Users currentUser);

    @Query("SELECT pl.post.id FROM PostLike pl WHERE pl.user = :user AND pl.post.id IN :postIds")
    Set<Long> findLikedPostIdsByUserAndPostIds(@Param("user") Users user, @Param("postIds") List<Long> postIds);

}
