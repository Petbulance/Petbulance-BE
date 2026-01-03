package com.example.Petbulance_BE.domain.notice.dto.response;

import com.example.Petbulance_BE.domain.notice.entity.Notice;
import com.example.Petbulance_BE.domain.notice.entity.NoticeFile;
import com.example.Petbulance_BE.global.util.TimeUtil;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.stream.Collectors;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DetailNoticeResDto {

    private Long noticeId;
    private String noticeStatus;
    private String title;
    private String createdAt;
    private String content;
    private List<AttachmentDto> attachments;
    private AdjacentNoticeDto previousNotice;
    private AdjacentNoticeDto nextNotice;

    public static DetailNoticeResDto from(Notice n, List<NoticeFile> files, Notice prev, Notice next) {
        return DetailNoticeResDto.builder()
                .noticeId(n.getId())
                .noticeStatus(n.getNoticeStatus().toString())
                .title(n.getTitle())
                .createdAt(TimeUtil.formatCreatedAt(n.getCreatedAt()))
                .content(n.getContent())
                .attachments(files.stream()
                        .map(AttachmentDto::from)
                        .collect(Collectors.toList()))
                .previousNotice(prev != null ? AdjacentNoticeDto.from(prev) : null)
                .nextNotice(next != null ? AdjacentNoticeDto.from(next) : null)
                .build();
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class AttachmentDto {
        private Long fileId;
        private String fileName;
        private String fileUrl;

        public static AttachmentDto from(NoticeFile f) {
            return AttachmentDto.builder()
                    .fileId(f.getId())
                    .fileName(f.getFileName())
                    .fileUrl(f.getFileUrl())
                    .build();
        }
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class AdjacentNoticeDto {
        private Long noticeId;
        private String title;

        public static AdjacentNoticeDto from(Notice n) {
            return AdjacentNoticeDto.builder()
                    .noticeId(n.getId())
                    .title(n.getTitle())
                    .build();
        }
    }
}
