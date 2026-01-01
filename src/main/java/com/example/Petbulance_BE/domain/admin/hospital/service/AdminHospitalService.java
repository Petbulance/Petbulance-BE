package com.example.Petbulance_BE.domain.admin.hospital.service;

import com.example.Petbulance_BE.domain.admin.hospital.dto.AdminHospitalDetailResDto;
import com.example.Petbulance_BE.domain.admin.hospital.dto.AdminHospitalListResDto;
import com.example.Petbulance_BE.domain.admin.hospital.dto.AdminSaveHospitalReqDto;
import com.example.Petbulance_BE.domain.admin.hospital.dto.HospitalWorktimeResDto;
import com.example.Petbulance_BE.domain.admin.hospital.dto.page.PageResponse;
import com.example.Petbulance_BE.domain.hospital.entity.Hospital;
import com.example.Petbulance_BE.domain.hospital.entity.Tag;
import com.example.Petbulance_BE.domain.hospital.repository.HospitalJpaRepository;
import com.example.Petbulance_BE.domain.hospital.repository.TagJpaRepository;
import com.example.Petbulance_BE.domain.hospitalWorktime.entity.HospitalWorktime;
import com.example.Petbulance_BE.domain.hospitalWorktime.entity.HospitalWorktimeKey;
import com.example.Petbulance_BE.domain.hospitalWorktime.repository.HospitalWorktimeJpaRepository;
import com.example.Petbulance_BE.domain.treatmentAnimal.entity.TreatmentAnimal;
import com.example.Petbulance_BE.domain.treatmentAnimal.repository.TreatmentAnimalJpaRepository;
import com.example.Petbulance_BE.global.common.error.exception.CustomException;
import com.example.Petbulance_BE.global.common.error.exception.ErrorCode;
import com.example.Petbulance_BE.global.common.type.AnimalType;
import lombok.RequiredArgsConstructor;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.Point;
import org.locationtech.jts.geom.PrecisionModel;
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
    private final HospitalWorktimeJpaRepository hospitalWorktimeJpaRepository;
    private final TagJpaRepository tagJpaRepository;
    private final TreatmentAnimalJpaRepository treatmentAnimalJpaRepository;


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

    @Transactional
    public Long saveHospitalProcess(AdminSaveHospitalReqDto ah) {

        GeometryFactory geometryFactory = new GeometryFactory(new PrecisionModel(), 4326);

        Point point = geometryFactory.createPoint(new Coordinate(ah.getLon(), ah.getLat()));

        Hospital hospital = Hospital.builder()
                .name(ah.getHospitalName())
                .address(ah.getAddress())
                .streetAddress(ah.getStreetAddress())
                .phoneNumber(ah.getPhoneNumber())
                .lat(ah.getLat())
                .lng(ah.getLon())
                .location(point)
                .url(ah.getUrl())
                .image(ah.getImage())
                .information(ah.getInformation())
                .nighCare(ah.getNight())
                .twentyFourHours(ah.getTwentyFour())
                .build();
        hospitalJpaRepository.save(hospital);

        List<HospitalWorktime> list = ah.getOperationTimes().stream().map(time -> HospitalWorktime.builder()
                        .id(new HospitalWorktimeKey(hospital.getId(), time.getDayOfWeek()))
                        .isOpen(time.getIsOpen())
                        .openTime(time.getOpenTime())
                        .closeTime(time.getCloseTime())
                        .breakStartTime(time.getStartBreakTime())
                        .breakEndTime(time.getEndBreakTime())
                        .receptionDeadline(time.getDeadLineTime())
                        .hospital(hospital)
                        .build())
                .toList();

        hospitalWorktimeJpaRepository.saveAll(list);

        List<TreatmentAnimal> animalList1 = ah.getAnimalTypes().stream().map(animal -> TreatmentAnimal.builder()
                        .animalType(animal)
                        .hospital(hospital)
                        .build())
                .toList();

        treatmentAnimalJpaRepository.saveAll(animalList1);

        List<Tag> tagsList = ah.getTags().stream().map(tag -> Tag.builder()
                .hospital(hospital)
                .tag(tag.replace("#", "").trim())
                .build()
        ).toList();

        tagJpaRepository.saveAll(tagsList);

        return hospital.getId();

    }

    @Transactional
    public Long updateHospitalProcess(Long id, AdminSaveHospitalReqDto ah) {

        Hospital hospital = hospitalJpaRepository.findById(id)
                .orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND_HOSPITAL));

        GeometryFactory geometryFactory = new GeometryFactory(new PrecisionModel(), 4326);
        Point point = geometryFactory.createPoint(new Coordinate(ah.getLon(), ah.getLat()));

        hospital.setName(ah.getHospitalName());
        hospital.setAddress(ah.getAddress());
        hospital.setStreetAddress(ah.getStreetAddress());
        hospital.setPhoneNumber(ah.getPhoneNumber());
        hospital.setLat(ah.getLat());
        hospital.setLng(ah.getLon());
        hospital.setLocation(point);
        hospital.setUrl(ah.getUrl());
        hospital.setImage(ah.getImage());
        hospital.setInformation(ah.getInformation());
        hospital.setNighCare(ah.getNight());
        hospital.setTwentyFourHours(ah.getTwentyFour());


        hospitalWorktimeJpaRepository.deleteByHospital(hospital);
        List<HospitalWorktime> worktimeList = ah.getOperationTimes().stream().map(time ->
                HospitalWorktime.builder()
                        .id(new HospitalWorktimeKey(hospital.getId(), time.getDayOfWeek()))
                        .isOpen(time.getIsOpen())
                        .openTime(time.getOpenTime())
                        .closeTime(time.getCloseTime())
                        .breakStartTime(time.getStartBreakTime())
                        .breakEndTime(time.getEndBreakTime())
                        .receptionDeadline(time.getDeadLineTime())
                        .hospital(hospital)
                        .build()
        ).toList();
        hospitalWorktimeJpaRepository.saveAll(worktimeList);

        treatmentAnimalJpaRepository.deleteByHospital(hospital);
        List<TreatmentAnimal> animalList = ah.getAnimalTypes().stream().map(animal ->
                TreatmentAnimal.builder()
                        .animalType(animal)
                        .hospital(hospital)
                        .build()
        ).toList();
        treatmentAnimalJpaRepository.saveAll(animalList);

        tagJpaRepository.deleteByHospital(hospital);
        List<Tag> tagsList = ah.getTags().stream().map(tag ->
                Tag.builder()
                        .hospital(hospital)
                        .tag(tag.replace("#", "").trim())
                        .build()
        ).toList();
        tagJpaRepository.saveAll(tagsList);

        return hospital.getId();
    }

    @Transactional
    public boolean deleteHospitalProcess(Long id) {

        Long i = hospitalJpaRepository.deleteHospitalById(id);

        if(i>0){
            return true;
        }else {
            return false;
        }
    }
}
