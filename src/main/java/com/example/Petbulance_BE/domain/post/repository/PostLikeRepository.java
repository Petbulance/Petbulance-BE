package com.example.Petbulance_BE.domain.post.repository;

import com.example.Petbulance_BE.domain.post.entity.Post;
import com.example.Petbulance_BE.domain.post.entity.PostLike;
import com.example.Petbulance_BE.domain.user.entity.Users;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PostLikeRepository extends JpaRepository<PostLike, Long> {
    boolean existsByPostIdAndUser(Long postId, Users currentUser);

    Optional<PostLike> findByPostAndUser(Post post, Users currentUser);
}
