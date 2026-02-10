package com.example.Petbulance_BE.domain.post.dto.response;

import com.example.Petbulance_BE.domain.notice.entity.Notice;
import com.example.Petbulance_BE.domain.notice.type.NoticeStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.domain.Slice;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PagingPostListResDto {
    private NoticeBannerInfo noticeBanner;
    private Long lastPostId;
    private List<PostListResDto> content;
    private boolean hasNext;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class NoticeBannerInfo {
        private Long noticeId;
        private NoticeStatus noticeStatus;
        private String title;
        private String content;

        public static NoticeBannerInfo from(Notice notice) {
            NoticeBannerInfo dto = new NoticeBannerInfo();
            dto.noticeId = notice.getId();
            dto.noticeStatus = notice.getNoticeStatus();
            dto.title = notice.getTitle();
            dto.content = notice.getContent();
            return dto;
        }
    }

    public PagingPostListResDto(Slice<PostListResDto> slice) {
        this.content = slice.getContent();
        this.hasNext = slice.hasNext();

        if (this.content != null && !this.content.isEmpty()) {
            this.lastPostId = this.content.get(this.content.size() - 1).getPostId();
        } else {
            this.lastPostId = null;
        }
    }
}
