package com.example.Petbulance_BE.domain.review.entity;

import com.example.Petbulance_BE.domain.user.entity.Users;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
@Table(name = "user_review_likes",
        uniqueConstraints = {@UniqueConstraint(columnNames = {"user_id","review_id"})})
public class UserReviewLike {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private Users user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "review_id")
    private UserReview review;

}
