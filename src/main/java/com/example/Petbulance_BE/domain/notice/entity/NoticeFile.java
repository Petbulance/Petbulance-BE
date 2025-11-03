package com.example.Petbulance_BE.domain.notice.entity;

import com.example.Petbulance_BE.global.common.mapped.BaseTimeEntity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "notice_files")
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class NoticeFile extends BaseTimeEntity{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "notice_file_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "notice_id", nullable = false)
    private Notice notice;
    private String fileUrl;
    private String fileName;
    private String fileType;
}
