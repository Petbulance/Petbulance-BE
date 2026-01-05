package com.example.Petbulance_BE.domain.notice.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.net.URL;

@Getter
@AllArgsConstructor
public class FileDownloadResDto {
    private URL downloadUrl;
    private String fileName;
}