package com.example.Petbulance_BE.domain.review.entity;

import com.example.Petbulance_BE.domain.hospital.entity.Hospital;
import com.example.Petbulance_BE.domain.user.entity.Users;
import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "userRevies")
public class UserReview {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "hospital_id")
    private Hospital hospital;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private Users user;

    private LocalDateTime visitDate;

    private String animalType;

    private String treatmentService;

    private String detailReview;

    private Double overallRating;

    private Double facilityRating;

    private Double expertiseRating;

    private Double kindnessRating;

    private Long totalPrice;

    private String detailPrice;

}
