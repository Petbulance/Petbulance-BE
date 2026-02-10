package com.example.Petbulance_BE.domain.review.repository;

import com.example.Petbulance_BE.domain.review.entity.UserReviewLike;
import com.example.Petbulance_BE.domain.user.entity.Users;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ReviewLikeJpaRepository extends JpaRepository<UserReviewLike, Long> {

    Boolean existsByUserAndReviewId(Users currentUser, Long reviewId);

    Optional<UserReviewLike> findByUserAndReviewId(Users currentUser, Long reviewId);
}
