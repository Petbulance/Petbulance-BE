package com.example.Petbulance_BE.domain.banner.dto.request;

import com.example.Petbulance_BE.domain.notice.type.NoticeStatus;
import com.example.Petbulance_BE.domain.notice.type.PostStatus;
import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UpdateBannerReqDto {
    private NoticeStatus noticeStatus;
    private PostStatus postStatus;

    @NotBlank(message = "제목(title)은 필수입니다.")
    private String title;

    @NotNull(message = "시작일(startDate)은 필수입니다.")
    private LocalDate startDate;

    @NotNull(message = "종료일(endDate)은 필수입니다.")
    private LocalDate endDate;

    @NotNull(message = "공지사항 id는 필수입니다.")
    private Long noticeId;

    private String fileUrl;

    /**
     * ✅ 종료일은 시작일보다 이후여야 함
     */
    @AssertTrue(message = "종료일은 시작일 이후 날짜여야 합니다.")
    public boolean isValidPeriod() {
        if (startDate == null || endDate == null) {
            return true; // @NotNull에서 이미 걸러짐
        }
        return endDate.isAfter(startDate);
    }
}
