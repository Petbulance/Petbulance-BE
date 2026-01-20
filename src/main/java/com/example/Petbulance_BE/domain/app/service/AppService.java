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
            // 1. S3에 저장할 고유 키 생성 (noticeImage/UUID_파일명)
            String key = "image/" + UUID.randomUUID() + "_" + fileDto.getFilename();

            // 2. S3업로드용 Presigned URL 생성 (만료시간 5분 등 설정)
            URL presignedUrl = s3Service.createPresignedPutUrl(key, fileDto.getContentType(), 300);

            // 3. 나중에 DB 저장 및 조회에 사용할 URL 구성
            // (S3Service에서 구현한 getObject 또는 직접 문자열 구성)
            String fileUrl = getFileUrlFromPresignedUrl(presignedUrl);

            urlInfos.add(new GetPresignResDto.UrlInfo(
                    presignedUrl.toString(),
                    fileUrl
            ));
        }
        return new GetPresignResDto(urlInfos);
    }

    public String getFileUrlFromPresignedUrl(URL presignedUrl) {
        String url = presignedUrl.toString();
        // "?" 가 시작되는 지점 앞부분만 잘라서 반환
        return url.contains("?") ? url.split("\\?")[0] : url;
    }
}
