package com.example.Petbulance_BE.domain.app.service;

import com.example.Petbulance_BE.domain.app.dto.MetadataDto;
import com.example.Petbulance_BE.domain.app.dto.MetadataRequestDto;
import com.example.Petbulance_BE.domain.app.dto.MetadataResponseDto;
import com.example.Petbulance_BE.domain.app.dto.RegionsResponseDto;
import com.example.Petbulance_BE.domain.app.dto.request.GetPresignReqDto;
import com.example.Petbulance_BE.domain.app.dto.response.GetPresignResDto;
import com.example.Petbulance_BE.domain.app.entity.App;
import com.example.Petbulance_BE.domain.app.repository.AppsJpaRepository;
import com.example.Petbulance_BE.domain.app.type.VersionType;
import com.example.Petbulance_BE.domain.board.repository.BoardRepository;
import com.example.Petbulance_BE.domain.region1.repository.Region1JpaRepository;
import com.example.Petbulance_BE.domain.region2.repository.Region2JpaRepository;
import com.example.Petbulance_BE.domain.species.repository.SpeciesJpaRepository;
import com.example.Petbulance_BE.global.common.error.exception.CustomException;
import com.example.Petbulance_BE.global.common.error.exception.ErrorCode;
import com.example.Petbulance_BE.global.common.s3.S3Service;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.net.URL;
import java.util.*;

@Service
@RequiredArgsConstructor
public class AppService {

    private final AppsJpaRepository appRepository;
    private final Region1JpaRepository region1JpaRepository;
    private final Region2JpaRepository region2JpaRepository;
    private final SpeciesJpaRepository speciesJpaRepository;
    private final BoardRepository boardRepository;
    private final AppsJpaRepository appsJpaRepository;
    private final S3Service s3Service;

    public Map<String, String> getVersionProcess() {
        App topByOrderByCreatedAtDesc = appsJpaRepository.findAppByVersionType(VersionType.APP).orElseThrow(()-> new CustomException(ErrorCode.NOT_FOUND_APP_VERSION));
        return Map.of("version",topByOrderByCreatedAtDesc.getVersion());
    }

    public MetadataResponseDto getMetadataProcess(MetadataRequestDto requestVersion) {

        MetadataDto metatDto = appRepository.findMetatDto();

        MetadataResponseDto metadataResponseDto = new MetadataResponseDto();

        if(!metatDto.getRegion().equals(requestVersion.getRegion())){
            List<String> region1Names = region1JpaRepository.findAllNames();
            List<String> region2Names = region2JpaRepository.findAllNames();

            RegionsResponseDto regionsResponseDto = new RegionsResponseDto(region1Names, region2Names);
            metadataResponseDto.setRegion(regionsResponseDto);
        }

        if(!metatDto.getSpecies().equals(requestVersion.getSpecies())){
            List<String> allTypes = speciesJpaRepository.findAllTypes();

            metadataResponseDto.setSpecies(allTypes);
        }

        if(!metatDto.getCommunity().equals(requestVersion.getCommunityCategory())){
            List<String> allName = boardRepository.findAllNameKr();

            metadataResponseDto.setCommunityCategory(allName);
        }
        return metadataResponseDto;
    }

    public GetPresignResDto getPresignedUrl(GetPresignReqDto reqDto) {
        List<GetPresignResDto.UrlInfo> urlInfos = new ArrayList<>();

        for (GetPresignReqDto.NoticeFileReqDto fileDto : reqDto.getFiles()) {
            String usageName = fileDto.getUsage().name(); // enum은 name()가 안전
            boolean isTest = usageName.startsWith("TEST_");

            String baseFolder = isTest
                    ? usageName.substring("TEST_".length())  // NOTICE_FILE / BANNER / POST
                    : usageName;

            String folderPath = (isTest ? "test/" : "") + baseFolder.toLowerCase();
            String sanitizedFilename = sanitizeFilename(fileDto.getFilename());
            String key = folderPath + "/" + UUID.randomUUID() + "_" + sanitizedFilename;

            URL presignedUrl = s3Service.createPresignedPutUrl(key, fileDto.getContentType(), 300);

            String fileUrl = getFileUrlFromPresignedUrl(presignedUrl);

            urlInfos.add(new GetPresignResDto.UrlInfo(
                    presignedUrl.toString(),
                    fileUrl,
                    fileDto.getOrder()
            ));
        }
        return new GetPresignResDto(urlInfos);
    }

    public String getFileUrlFromPresignedUrl(URL presignedUrl) {
        String url = presignedUrl.toString();
        // "?" 가 시작되는 지점 앞부분만 잘라서 반환
        return url.contains("?") ? url.split("\\?")[0] : url;
    }

    private String sanitizeFilename(String filename) {
        if (filename == null) return UUID.randomUUID().toString();

        // 확장자 분리
        String extension = "";
        String name = filename;
        int dotIndex = filename.lastIndexOf(".");
        if (dotIndex >= 0) {
            extension = filename.substring(dotIndex).toLowerCase();
            name = filename.substring(0, dotIndex);
        }

        // 한글 포함 여부 확인
        boolean hasKorean = name.chars()
                .anyMatch(c -> (c >= 0xAC00 && c <= 0xD7A3)  // 완성형 한글
                        || (c >= 0x1100 && c <= 0x11FF)        // 한글 자모
                        || (c >= 0x3130 && c <= 0x318F));       // 한글 호환 자모

        if (hasKorean) {
            // 한글이 있으면 UUID로 대체
            return UUID.randomUUID() + extension;
        }

        // 한글 없으면 원본 파일명 유지 (공백만 _ 로 교체)
        return name.replaceAll("\\s+", "_") + extension;
    }
}
