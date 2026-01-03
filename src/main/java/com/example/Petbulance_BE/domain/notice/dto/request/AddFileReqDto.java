package com.example.Petbulance_BE.domain.notice.dto.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AddFileReqDto {
    private List<NoticeFileReqDto> addFiles;
    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public class NoticeFileReqDto {
        private String filename;
        private String contentType;
    }

}
