package com.example.Petbulance_BE.domain.review.entity;

import com.example.Petbulance_BE.domain.user.entity.Users;
import jakarta.persistence.*;

@Entity
@Table(name = "review_reports")
public class ReviewReport {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private Users user;

    @ManyToOne
    @JoinColumn(name = "review_id", nullable = false)
    private UserReview review;

    private String comment;

}
