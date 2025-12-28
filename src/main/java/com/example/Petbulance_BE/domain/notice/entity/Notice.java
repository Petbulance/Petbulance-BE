package com.example.Petbulance_BE.domain.notice.entity;

import com.example.Petbulance_BE.domain.notice.dto.request.UpdateNoticeReqDto;
import com.example.Petbulance_BE.domain.notice.type.NoticeStatus;
import com.example.Petbulance_BE.domain.notice.type.PostStatus;
import com.example.Petbulance_BE.global.common.mapped.BaseTimeEntity;
import jakarta.persistence.*;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "notices")
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Notice extends BaseTimeEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "notice_id")
    private Long id;

    @Enumerated(EnumType.STRING)
    private NoticeStatus noticeStatus;

    @Enumerated(EnumType.STRING)
    private PostStatus postStatus;

    @Column(length = 200)
    private String title;

    @Column(columnDefinition = "TEXT")
    private String content;

    private LocalDate postStartDate;
    private LocalDate postEndDate;

    @OneToMany(
            mappedBy = "notice",
            cascade = CascadeType.ALL,
            orphanRemoval = true,
            fetch = FetchType.LAZY
    )
    @Builder.Default
    private List<NoticeFile> files = new ArrayList<>();

    public void addFile(NoticeFile file) {
        files.add(file);
        file.setNotice(this);
    }

    public void removeFile(NoticeFile file) {
        files.remove(file);
        file.setNotice(null);
    }

    public void update(@Valid UpdateNoticeReqDto reqDto) {
        this.noticeStatus = reqDto.getNoticeStatus();
        this.postStatus = reqDto.getPostStatus();
        this.title = reqDto.getTitle();
        this.content = reqDto.getContent();
        this.postStartDate = reqDto.getStartDate();
        this.postEndDate = reqDto.getEndDate();
    }
}
