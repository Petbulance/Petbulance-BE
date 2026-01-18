package com.example.Petbulance_BE.domain.notice.dto.request;

import com.example.Petbulance_BE.domain.notice.type.NoticeStatus;
import com.example.Petbulance_BE.domain.notice.type.PostStatus;
import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CreateNoticeReqDto {

    private NoticeStatus noticeStatus;
    private PostStatus postStatus;

    @NotBlank(message = "제목(title)은 필수입니다.")
    private String title;

    @NotBlank(message = "내용(content)은 비워둘 수 없습니다.")
    private String content;

    @Size(min = 1, max = 5, message = "첨부파일은 1개 이상 5개 이하만 가능합니다.")
    private List<NoticeFileReqDto> files;

    private boolean bannerRegistered; // 배너 설정 여부

    private BannerReqDto bannerInfo;


    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class NoticeFileReqDto {
        private String filename;
        private String contentType;
    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class BannerReqDto {
        private LocalDate startDate;
        private LocalDate endDate;
        private String imageName;
        private String imageContentType;
    }

    @AssertTrue(message = "배너 시작일은 종료일보다 이전이거나 같아야 하며, 배너 정보는 필수입니다.")
    public boolean isValidBannerDate() {
        // 배너 등록이 false라면 검증 통과
        if (!bannerRegistered) {
            return true;
        }

        // 배너 등록이 true인데 bannerInfo가 없으면 검증 실패
        if (bannerInfo == null || bannerInfo.getStartDate() == null || bannerInfo.getEndDate() == null) {
            return false;
        }

        // startDate가 endDate보다 이전(isBefore)이거나 같은 날(isEqual)인지 확인
        return !bannerInfo.getStartDate().isAfter(bannerInfo.getEndDate());
    }
}
