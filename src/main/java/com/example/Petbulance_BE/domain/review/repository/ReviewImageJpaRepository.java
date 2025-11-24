package com.example.Petbulance_BE.domain.review.repository;

import com.example.Petbulance_BE.domain.review.entity.UserReviewImage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ReviewImageJpaRepository extends JpaRepository<UserReviewImage, Long> {
}
