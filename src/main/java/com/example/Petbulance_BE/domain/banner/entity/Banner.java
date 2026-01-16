package com.example.Petbulance_BE.domain.banner.entity;

import com.example.Petbulance_BE.domain.notice.entity.Notice;
import com.example.Petbulance_BE.domain.notice.type.NoticeStatus;
import com.example.Petbulance_BE.domain.notice.type.PostStatus;
import com.example.Petbulance_BE.domain.user.entity.Users;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Entity
@Table(name = "banner")
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Banner {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "notice_id")
    private Notice notice;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private Users users;

    @Enumerated(EnumType.STRING)
    private PostStatus postStatus;

    @Enumerated(EnumType.STRING)
    private NoticeStatus noticeStatus;

    private String title;

    private LocalDate startDate;
    private  LocalDate endDate;

    @Builder.Default
    private String fileUrl = null;

    public void update(NoticeStatus noticeStatus, PostStatus postStatus, Notice notice, String title, LocalDate startDate, LocalDate endDate) {
        this.noticeStatus = noticeStatus;
        this.postStatus = postStatus;
        this.notice = notice;
        this.title = title;
        this.startDate = startDate;
        this.endDate = endDate;
    }

    public void updateFileUrl(String key) {
        this.fileUrl = key;
    }
}
