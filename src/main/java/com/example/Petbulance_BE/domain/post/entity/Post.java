package com.example.Petbulance_BE.domain.post.entity;

import com.example.Petbulance_BE.domain.post.type.Topic;
import com.example.Petbulance_BE.domain.user.entity.Users;
import com.example.Petbulance_BE.global.common.mapped.BaseTimeEntity;
import com.example.Petbulance_BE.global.common.type.AnimalType;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "posts")
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Post extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "post_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private Users user;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Topic topic;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private AnimalType animalType;

    @Column(nullable = false, length = 255)
    private String title;

    @Column(columnDefinition = "TEXT", nullable = true)
    private String content;

    @Column(nullable = false)
    @Builder.Default
    private boolean hidden = false;

    @Column(nullable = false)
    @Builder.Default
    private boolean deleted = false;


    @Column(name = "image_num", nullable = false)
    private int imageNum;

    @Builder.Default
    private int reportCount = 0;

    // 양방향 연관관계
    @Builder.Default
    @OneToMany(mappedBy = "post", cascade = CascadeType.REMOVE, orphanRemoval = true)
    private List<PostImage> postImages = new ArrayList<>();

    public void update(String title, String content, Topic topic, AnimalType type, int imageNum) {
       this.title = title;
       this.content = content;
       this.topic = topic;
       this.animalType = type;
       this.imageNum = imageNum;
    }

    public void increaseReportCount() {
        reportCount++;
    }

    public void updateHidden() {
        if(!hidden) hidden = true;
    }
}

