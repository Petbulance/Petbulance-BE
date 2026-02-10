package com.example.Petbulance_BE.domain.app.dto.request;

import com.example.Petbulance_BE.domain.app.type.FileUsage;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class GetPresignReqDto {
    private List<NoticeFileReqDto> files;

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class NoticeFileReqDto {
        private FileUsage usage;
        private String filename;
        private String contentType;
        private int order;
    }
}
