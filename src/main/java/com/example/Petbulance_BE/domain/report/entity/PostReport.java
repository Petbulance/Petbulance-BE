package com.example.Petbulance_BE.domain.report.entity;

import com.example.Petbulance_BE.domain.post.entity.Post;
import jakarta.persistence.*;
import lombok.experimental.SuperBuilder;

@Entity
@SuperBuilder
@DiscriminatorValue("POST")
public class PostReport extends Report {

    @ManyToOne(fetch = FetchType.LAZY)
    @Column(name = "post_id", nullable = false)
    private Post post;
}

