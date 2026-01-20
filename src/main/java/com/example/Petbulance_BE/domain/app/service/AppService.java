package com.example.Petbulance_BE.domain.app.service;

import com.example.Petbulance_BE.domain.app.dto.MetadataDto;
import com.example.Petbulance_BE.domain.app.dto.MetadataRequestDto;
import com.example.Petbulance_BE.domain.app.dto.MetadataResponseDto;
import com.example.Petbulance_BE.domain.app.dto.RegionsResponseDto;
import com.example.Petbulance_BE.domain.app.entity.App;
import com.example.Petbulance_BE.domain.app.repository.AppsJpaRepository;
import com.example.Petbulance_BE.domain.app.type.VersionType;
import com.example.Petbulance_BE.domain.board.repository.BoardRepository;
import com.example.Petbulance_BE.domain.region1.repository.Region1JpaRepository;
import com.example.Petbulance_BE.domain.region2.repository.Region2JpaRepository;
import com.example.Petbulance_BE.domain.species.repository.SpeciesJpaRepository;
import com.example.Petbulance_BE.global.common.error.exception.CustomException;
import com.example.Petbulance_BE.global.common.error.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AppService {

    private final AppsJpaRepository appRepository;
    private final Region1JpaRepository region1JpaRepository;
    private final Region2JpaRepository region2JpaRepository;
    private final SpeciesJpaRepository speciesJpaRepository;
    private final BoardRepository boardRepository;
    private final AppsJpaRepository appsJpaRepository;

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
}
