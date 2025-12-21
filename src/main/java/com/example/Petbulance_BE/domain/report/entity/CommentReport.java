package com.example.Petbulance_BE.domain.report.entity;

import com.example.Petbulance_BE.domain.comment.entity.PostComment;
import jakarta.persistence.*;
import lombok.experimental.SuperBuilder;

@Entity
@SuperBuilder
@DiscriminatorValue("COMMENT")
public class CommentReport extends Report {

    @ManyToOne(fetch = FetchType.LAZY)
    @Column(name = "comment_id", nullable = false)
    private PostComment postComment;
}
