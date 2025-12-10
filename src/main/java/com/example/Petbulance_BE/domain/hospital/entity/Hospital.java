package com.example.Petbulance_BE.domain.hospital.entity;

import com.example.Petbulance_BE.domain.hospitalWorktime.entity.HospitalWorktime;
import com.example.Petbulance_BE.domain.review.entity.UserReview;
import com.example.Petbulance_BE.domain.treatmentAnimal.entity.TreatmentAnimal;
import com.example.Petbulance_BE.global.common.mapped.BaseTimeEntity;
import jakarta.persistence.*;
import lombok.*;

import org.locationtech.jts.geom.Point;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Entity
@Table(name = "hospitals",
indexes = {
        @Index(name = "idx_location", columnList = "location")
})
@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Hospital extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    private String address;

    private String streetAddress;

    private String phoneNumber;

    private String information;

    @Column(columnDefinition = "POINT SRID 4326", nullable = false)
    private Point location;

    private Double lat;

    private Double lng;

    @Column(length = 500)
    private String url;

    @Column(length = 500)
    private String image;

    private boolean nighCare;

    private boolean twentyFourHours;

    //일대다 다중 페치조인은 list불가 set으로 지정
    @Builder.Default
    @OneToMany(mappedBy = "hospital")
    @OrderBy("id ASC ")
    private Set<UserReview> userReviews = new HashSet<>();

    @Builder.Default
    @OrderBy("id ASC")
    @OneToMany(mappedBy = "hospital", cascade = CascadeType.REMOVE, orphanRemoval = true)
    private Set<HospitalWorktime> hospitalWorktimes = new HashSet<>();

    @Builder.Default
    @OneToMany(mappedBy = "hospital", cascade = CascadeType.REMOVE, orphanRemoval = true)
    @OrderBy("id ASC")
    private Set<TreatmentAnimal> treatmentAnimals = new HashSet<>();

    @OneToMany(mappedBy = "hospital")
    @Builder.Default
    @OrderBy("id ASC")
    private Set<Tag> tags = new HashSet<>();

}
