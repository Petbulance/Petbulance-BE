package com.example.Petbulance_BE.domain.comment.repository;

import com.example.Petbulance_BE.domain.comment.entity.PostComment;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PostCommentRepository extends JpaRepository<PostComment, Long> {
}
