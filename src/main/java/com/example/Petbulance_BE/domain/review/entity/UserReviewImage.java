package com.example.Petbulance_BE.domain.review.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "userReviewImages")
public class UserReviewImage {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String imageUrl;

    @JoinColumn(name = "reivew_id")
    @ManyToOne(fetch = FetchType.LAZY)
    private UserReview review;


}
