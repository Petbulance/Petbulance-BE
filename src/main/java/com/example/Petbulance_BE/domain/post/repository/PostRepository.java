package com.example.Petbulance_BE.domain.post.repository;

import com.example.Petbulance_BE.domain.post.entity.Post;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PostRepository extends JpaRepository<Post, Long> {
}
