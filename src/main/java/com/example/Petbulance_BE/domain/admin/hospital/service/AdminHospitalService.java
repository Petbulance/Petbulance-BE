package com.example.Petbulance_BE.domain.admin.hospital.service;

import com.example.Petbulance_BE.domain.admin.hospital.dto.AdminHospitalDetailResDto;
import com.example.Petbulance_BE.domain.admin.hospital.dto.AdminHospitalListResDto;
import com.example.Petbulance_BE.domain.admin.hospital.dto.HospitalWorktimeResDto;
import com.example.Petbulance_BE.domain.admin.hospital.dto.page.PageResponse;
import com.example.Petbulance_BE.domain.hospital.entity.Hospital;
import com.example.Petbulance_BE.domain.hospital.entity.Tag;
import com.example.Petbulance_BE.domain.hospital.repository.HospitalJpaRepository;
import com.example.Petbulance_BE.domain.hospitalWorktime.entity.HospitalWorktime;
import com.example.Petbulance_BE.domain.treatmentAnimal.entity.TreatmentAnimal;
import com.example.Petbulance_BE.global.common.error.exception.CustomException;
import com.example.Petbulance_BE.global.common.error.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AdminHospitalService {


    private final List<String> DAY_ORDER = List.of("SUN", "MON", "TUE", "WED", "THU", "FRI", "SAT");
    private final HospitalJpaRepository hospitalJpaRepository;


    public PageResponse<AdminHospitalListResDto> findHospitalProcess(Pageable pageable) {
        Page<Hospital> hospitalPage = hospitalJpaRepository.findAll(pageable);

        Page<AdminHospitalListResDto> dtoPage = hospitalPage.map(hospital -> new AdminHospitalListResDto(hospital.getId(), hospital.getName()));

        return new PageResponse<>(dtoPage);

    }

    public PageResponse<AdminHospitalListResDto> findNameHospitalProcess(Pageable pageable, String name) {

        Page<Hospital> hospitalPage = hospitalJpaRepository.findByNameContaining(pageable, name);

        Page<AdminHospitalListResDto> dtoPage = hospitalPage.map(hospital -> new AdminHospitalListResDto(hospital.getId(), hospital.getName()));

        return new PageResponse<>(dtoPage);
    }

    @Transactional
    public AdminHospitalDetailResDto detailHospitalProcess(Long id) {

        Hospital hospital = hospitalJpaRepository.findById(id).orElseThrow(() -> new CustomException(ErrorCode.NON_EXIST_USER));
        List<HospitalWorktimeResDto> hospitalWorktimes = hospital.getHospitalWorktimes().stream().sorted(Comparator.comparingInt(worktime->DAY_ORDER.indexOf(worktime.getId().getDayOfWeek()))).map(HospitalWorktimeResDto::new).toList();
        String types = hospital.getTreatmentAnimals().stream().map(type -> type.getAnimalType().toString()).collect(Collectors.joining(","));
        String tags = hospital.getTags().stream().map(tag -> "#" + tag.getTag()).collect(Collectors.joining(" "));

        return AdminHospitalDetailResDto.builder()
                    .name(hospital.getName())
                    .address(hospital.getAddress())
                    .streetAddress(hospital.getStreetAddress())
                    .phoneNumber(hospital.getPhoneNumber())
                    .information(hospital.getInformation())
                    .lat(hospital.getLat())
                    .lng(hospital.getLng())
                    .url(hospital.getUrl())
                    .image(hospital.getImage())
                    .nighCare(hospital.isNighCare())
                    .twentyFourHours(hospital.isTwentyFourHours())
                    .tag(tags)
                    .treatmentAnimalType(types)
                    .worktimes(hospitalWorktimes)
                    .build();

    }
}
