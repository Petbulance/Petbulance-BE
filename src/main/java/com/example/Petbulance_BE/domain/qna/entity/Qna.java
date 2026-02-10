package com.example.Petbulance_BE.domain.qna.entity;

import com.example.Petbulance_BE.domain.qna.type.QnaStatus;
import com.example.Petbulance_BE.domain.user.entity.Users;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "qna")
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Qna {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "qna_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private Users user;

    private String title;

    @Column(columnDefinition = "TEXT")
    private String content;

    @Enumerated(EnumType.STRING)
    private QnaStatus status;

    @Column(columnDefinition = "TEXT")
    @Builder.Default
    private String answerContent = "";

    private LocalDateTime createdAt;

    @Builder.Default
    private LocalDateTime answeredAt = null;

    public void answer(String answerContent) {
        this.answerContent = answerContent;
        this.status = QnaStatus.ANSWER_COMPLETED;
        this.answeredAt = LocalDateTime.now();
    }
}
