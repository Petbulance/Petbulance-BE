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

    private LocalDate startDate;
    private LocalDate endDate;

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class NoticeFileReqDto {
        private String filename;
        private String contentType;
    }

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
