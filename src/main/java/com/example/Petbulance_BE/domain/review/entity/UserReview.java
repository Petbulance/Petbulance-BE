package com.example.Petbulance_BE.domain.review.entity;

import com.example.Petbulance_BE.domain.hospital.entity.Hospital;
import com.example.Petbulance_BE.domain.user.entity.Users;
import com.example.Petbulance_BE.global.common.mapped.BaseTimeEntity;
import com.example.Petbulance_BE.global.common.type.AnimalType;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.BatchSize;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "userReviews")
public class UserReview extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Boolean receiptCheck;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "hospital_id")
    private Hospital hospital;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private Users user;

    private LocalDate visitDate;

    @Enumerated(EnumType.STRING)
    private AnimalType animalType;

    private String detailAnimalType;

    private String treatmentService;

    private String reviewContent;

    private Double overallRating;

    private Double facilityRating;

    private Double expertiseRating;

    private Double kindnessRating;

    private Long totalPrice;

    @Builder.Default
    private Boolean hidden = false;

    @Builder.Default
    private Boolean deleted = false;

    @Builder.Default
    @OneToMany(mappedBy = "review", cascade = CascadeType.REMOVE, orphanRemoval = true)
    @BatchSize(size = 10)
    List<UserReviewImage> images = new ArrayList<>();

    @BatchSize(size = 10)
    @OneToMany(mappedBy = "review", cascade = CascadeType.REMOVE, orphanRemoval = true)
    @Builder.Default
    List<UserReviewLike> likes = new ArrayList<>();


}
