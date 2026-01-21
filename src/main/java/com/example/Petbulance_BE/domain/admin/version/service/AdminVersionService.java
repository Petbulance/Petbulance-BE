package com.example.Petbulance_BE.domain.admin.version.service;

import com.example.Petbulance_BE.domain.admin.version.dto.*;
import com.example.Petbulance_BE.domain.admin.version.type.RegionType;
import com.example.Petbulance_BE.domain.app.entity.App;
import com.example.Petbulance_BE.domain.app.repository.AppsJpaRepository;
import com.example.Petbulance_BE.domain.app.type.VersionType;
import com.example.Petbulance_BE.domain.board.entity.Board;
import com.example.Petbulance_BE.domain.board.repository.BoardRepository;
import com.example.Petbulance_BE.domain.region1.entity.Region1;
import com.example.Petbulance_BE.domain.region1.repository.Region1JpaRepository;
import com.example.Petbulance_BE.domain.region2.entity.Region2;
import com.example.Petbulance_BE.domain.region2.repository.Region2JpaRepository;
import com.example.Petbulance_BE.domain.species.entity.Species;
import com.example.Petbulance_BE.domain.species.repository.SpeciesJpaRepository;
import com.example.Petbulance_BE.domain.terms.entity.Terms;
import com.example.Petbulance_BE.domain.terms.repository.TermsJpaRepository;
import com.example.Petbulance_BE.global.common.error.exception.CustomException;
import com.example.Petbulance_BE.global.common.error.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Stream;

@Service
@RequiredArgsConstructor
@Slf4j
public class AdminVersionService {

    private final Region1JpaRepository region1JpaRepository;
    private final Region2JpaRepository region2JpaRepository;
    private final AppsJpaRepository appsJpaRepository;
    private final SpeciesJpaRepository speciesJpaRepository;
    private final BoardRepository boardRepository;
    private final TermsJpaRepository termsJpaRepository;

    public RegionResDto getRegionProcess() {

        List<Region1> all = region1JpaRepository.findAll();

        List<RegionResDto.Region1Dto> list1 = all.stream().map(region1 -> RegionResDto.Region1Dto.builder()
                .id(region1.getId())
                .name(region1.getName())
                .code(region1.getCode())
                .build()).toList();

        List<Region2> all2 = region2JpaRepository.findAll();

        List<RegionResDto.Region2Dto> list2 = all2.stream().map(region2 -> RegionResDto.Region2Dto.builder()
                .id(region2.getId())
                .name(region2.getName())
                .code(region2.getCode())
                .superiorId(region2.getRegion1().getId())
                .build()).toList();

        return new RegionResDto(list1, list2);

    }

    @Transactional
    public Map<String, String> addRegionProcess(RegionAddReqDto regionAddReqDto) {

        RegionType regionType = regionAddReqDto.getType();

        if(regionType==RegionType.REGION1) {
            Region1 region1 = Region1.builder()
                    .name(regionAddReqDto.getName())
                    .code(regionAddReqDto.getCode())
                    .build();
            region1JpaRepository.save(region1);

        }else{
            Region1 superior = region1JpaRepository.getReferenceById(regionAddReqDto.getSuperior());
            Region2 region2 = Region2.builder()
                    .name(regionAddReqDto.getName())
                    .code(regionAddReqDto.getCode())
                    .region1(superior)
                    .build();
            region2JpaRepository.save(region2);

        }

        return Map.of("message", "success");

    }

    @Transactional
    public Map<String, String> deleteRegionProcess(RegionType type, Long id) {

        int i=0;

        if(type==RegionType.REGION1) {
            i = region1JpaRepository.deleteByIdAndReturnCount(id);
        }else{
            i = region2JpaRepository.deleteByIdAndReturnCount(id);
        }

        if(i>=1){
            App appByVersionType = appsJpaRepository.findAppByVersionType(VersionType.Region).orElseThrow(()-> new CustomException(ErrorCode.NOT_FOUND_APP_VERSION));

            return Map.of("message", "success");
        }

            throw new CustomException(ErrorCode.FAIL_DELETE_REGION);
    }

    public List<SpeciesResDto> getSpeciesProcess() {

        List<Species> all = speciesJpaRepository.findAll();

        return all.stream().map(s -> new SpeciesResDto(s.getId(), s.getType())).toList();

    }

    public List<CategoryResDto> getCategoryProcess() {

        List<Board> all = boardRepository.findAll();

        return all.stream().map(b -> new CategoryResDto(b.getId(), b.getNameEn())).toList();

    }

    @Transactional
    public Map<String, String> postTermsProcess(TermsReqDto termsReqDto) {

        Terms terms = new Terms();
        terms.setType(termsReqDto.getTermsType());
        terms.setContent(termsReqDto.getContent());
        terms.setVersion(termsReqDto.getVersion());
        terms.setIsRequired(termsReqDto.getIsRequired());
        terms.setIsActive(true);

        Boolean exists = termsJpaRepository.existsByVersion(termsReqDto.getVersion());

        if(exists) throw new CustomException(ErrorCode.ALREADY_EXIST_VERSION);

        Integer i = termsJpaRepository.updateUnActive(termsReqDto.getTermsType());

        termsJpaRepository.save(terms);

        return Map.of("message", "success");

    }
}
