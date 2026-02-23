package com.example.Petbulance_BE.domain.report.entity;

import com.example.Petbulance_BE.domain.report.type.ReportActionType;
import com.example.Petbulance_BE.domain.report.type.ReportStatus;
import com.example.Petbulance_BE.domain.report.type.ReportType;
import com.example.Petbulance_BE.domain.user.entity.Users;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "report")
public class Report {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long reportId;

    @Column(nullable = false)
    private String reportReason;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "reporter_id", nullable = false)
    private Users reporter; // 신고자

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "target_user_id", nullable = false)
    private Users targetUser; // 신고 대상자

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ReportType reportType;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private ReportStatus status;

    @Enumerated(EnumType.STRING)
    @Builder.Default
    private ReportActionType actionType = null;

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt; // 신고 날짜

    @Column(name = "post_id")
    @Builder.Default
    private Long postId = null;

    @Column(name = "comment_id")
    @Builder.Default
    private Long commentId = null;

    @Column(name = "review_id")
    @Builder.Default
    private Long reviewId = null;


    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
    }

    // 조치가 취해질 때
    public void publish() {
        this.status = ReportStatus.COMPLETED; // 처리 완료
        this.actionType = ReportActionType.PUBLISH; // 게시 처리는 별다른 조치 없음
    }

    public void deleteAction(ReportActionType reportActionType) {
        this.status = ReportStatus.COMPLETED;
        this.actionType = reportActionType;
    }
}

