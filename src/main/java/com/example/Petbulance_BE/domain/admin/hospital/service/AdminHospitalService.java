package com.example.Petbulance_BE.domain.admin.hospital.service;

import com.example.Petbulance_BE.domain.admin.hospital.dto.AdminHospitalDetailResDto;
import com.example.Petbulance_BE.domain.admin.hospital.dto.AdminHospitalListResDto;
import com.example.Petbulance_BE.domain.admin.hospital.dto.AdminSaveHospitalReqDto;
import com.example.Petbulance_BE.domain.admin.hospital.dto.HospitalWorktimeResDto;
import com.example.Petbulance_BE.domain.admin.hospital.entity.HospitalHistory;
import com.example.Petbulance_BE.domain.admin.hospital.repository.HospitalsHistoryJpaRepository;
import com.example.Petbulance_BE.domain.admin.page.PageResponse;
import com.example.Petbulance_BE.domain.hospital.dto.res.HospitalDetailResDto;
import com.example.Petbulance_BE.domain.hospital.entity.Hospital;
import com.example.Petbulance_BE.domain.hospital.entity.Tag;
import com.example.Petbulance_BE.domain.hospital.repository.HospitalJpaRepository;
import com.example.Petbulance_BE.domain.hospital.repository.TagJpaRepository;
import com.example.Petbulance_BE.domain.hospitalWorktime.entity.HospitalWorktime;
import com.example.Petbulance_BE.domain.hospitalWorktime.entity.HospitalWorktimeKey;
import com.example.Petbulance_BE.domain.hospitalWorktime.repository.HospitalWorktimeJpaRepository;
import com.example.Petbulance_BE.domain.treatmentAnimal.entity.TreatmentAnimal;
import com.example.Petbulance_BE.domain.treatmentAnimal.repository.TreatmentAnimalJpaRepository;
import com.example.Petbulance_BE.domain.user.entity.Users;
import com.example.Petbulance_BE.global.common.error.exception.CustomException;
import com.example.Petbulance_BE.global.common.error.exception.ErrorCode;
import com.example.Petbulance_BE.global.util.UserUtil;
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

    private final HospitalsHistoryJpaRepository hospitalsHistoryJpaRepository;
    private final List<String> DAY_ORDER = List.of("SUN", "MON", "TUE", "WED", "THU", "FRI", "SAT");
    private final HospitalJpaRepository hospitalJpaRepository;
    private final HospitalWorktimeJpaRepository hospitalWorktimeJpaRepository;
    private final TagJpaRepository tagJpaRepository;
    private final TreatmentAnimalJpaRepository treatmentAnimalJpaRepository;
    private final UserUtil userUtil;

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
        List<HospitalHistory> hospitalHistory = hospitalsHistoryJpaRepository.findByHospitalId(id);
        List<HospitalWorktimeResDto> hospitalWorktimes = hospital.getHospitalWorktimes().stream().sorted(Comparator.comparingInt(worktime->DAY_ORDER.indexOf(worktime.getId().getDayOfWeek()))).map(HospitalWorktimeResDto::new).toList();
        String types = hospital.getTreatmentAnimals().stream().map(type -> type.getAnimalType().toString()).collect(Collectors.joining(","));
        String tags = hospital.getTags().stream().map(tag -> "#" + tag.getTag()).collect(Collectors.joining(" "));
        List<AdminHospitalDetailResDto.HospitalHistoriesResDto> list = hospitalHistory.stream().map(hh -> AdminHospitalDetailResDto.HospitalHistoriesResDto.builder()
                .hospitalId(hh.getHospital().getId())
                .modifySubject(hh.getModifySubject())
                .beforeModify(hh.getBeforeModify())
                .afterModify(hh.getAfterModify())
                .actorId(hh.getUsers().getId())
                .createdAt(hh.getCreatedAt())
                .build())
                .toList();

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
                    .hospitalHistories(list)
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

        Users currentUser = userUtil.getCurrentUser();

        Hospital hospital = hospitalJpaRepository.findById(id)
                .orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND_HOSPITAL));

        // 변경 이력을 저장할 리스트
        List<HospitalHistory> histories = new ArrayList<>();

        // 1. 기본 정보 비교 및 히스토리 생성
        compareAndAddHistory(histories, hospital, "동물병원명", hospital.getName(), ah.getHospitalName(), currentUser);
        compareAndAddHistory(histories, hospital, "지번주소", hospital.getAddress(), ah.getAddress(), currentUser);
        compareAndAddHistory(histories, hospital, "도로명주소", hospital.getStreetAddress(), ah.getStreetAddress(), currentUser);
        compareAndAddHistory(histories, hospital, "전화번호", hospital.getPhoneNumber(), ah.getPhoneNumber(), currentUser);
        compareAndAddHistory(histories, hospital, "위도", String.valueOf(hospital.getLat()), String.valueOf(ah.getLat()), currentUser);
        compareAndAddHistory(histories, hospital, "경도", String.valueOf(hospital.getLng()), String.valueOf(ah.getLon()), currentUser);
        compareAndAddHistory(histories, hospital, "홈페이지 URL", hospital.getUrl(), ah.getUrl(), currentUser);
        compareAndAddHistory(histories, hospital, "이미지 URL", hospital.getImage(), ah.getImage(), currentUser);
        compareAndAddHistory(histories, hospital, "병원정보", hospital.getInformation(), ah.getInformation(), currentUser);
        compareAndAddHistory(histories, hospital, "야간진료", String.valueOf(hospital.isNighCare()), String.valueOf(ah.getNight()), currentUser);
        compareAndAddHistory(histories, hospital, "24시간", String.valueOf(hospital.isTwentyFourHours()), String.valueOf(ah.getTwentyFour()), currentUser);

        // 2. 운영시간 비교 (정렬 추가하여 순서 변경으로 인한 로그 방지)
        String beforeTimes = hospital.getHospitalWorktimes().stream()
                .sorted(Comparator.comparingInt(w -> DAY_ORDER.indexOf(w.getId().getDayOfWeek())))
                .map(w -> String.format("[%s: %s, %s-%s, 휴식:%s-%s, 접수마감:%s]",
                        w.getId().getDayOfWeek(),
                        w.getIsOpen() ? "영업" : "휴무",
                        w.getOpenTime(), w.getCloseTime(),
                        w.getBreakStartTime(), w.getBreakEndTime(),
                        w.getReceptionDeadline()))
                .collect(Collectors.joining(" / "));

        String afterTimes = ah.getOperationTimes().stream()
                .sorted(Comparator.comparingInt(t -> DAY_ORDER.indexOf(t.getDayOfWeek())))
                .map(t -> String.format("[%s: %s, %s-%s, 휴식:%s-%s, 접수마감:%s]",
                        t.getDayOfWeek(),
                        t.getIsOpen() ? "영업" : "휴무",
                        t.getOpenTime(), t.getCloseTime(),
                        t.getStartBreakTime(), t.getEndBreakTime(),
                        t.getDeadLineTime()))
                .collect(Collectors.joining(" / "));

        compareAndAddHistory(histories, hospital, "운영시간", beforeTimes, afterTimes, currentUser);

        // 3. 진료 동물 비교 (추가)
        String beforeAnimals = hospital.getTreatmentAnimals().stream()
                .map(a -> a.getAnimalType().toString())
                .sorted()
                .collect(Collectors.joining(", "));
        String afterAnimals = ah.getAnimalTypes().stream()
                .map(Enum::toString)
                .sorted()
                .collect(Collectors.joining(", "));
        compareAndAddHistory(histories, hospital, "진료동물", beforeAnimals, afterAnimals, currentUser);

        // 4. 태그 비교 (추가)
        String beforeTags = hospital.getTags().stream()
                .map(Tag::getTag)
                .sorted()
                .collect(Collectors.joining(", "));
        String afterTags = ah.getTags().stream()
                .map(tag -> tag.replace("#", "").trim())
                .sorted()
                .collect(Collectors.joining(", "));
        compareAndAddHistory(histories, hospital, "태그", beforeTags, afterTags, currentUser);

        // ------------------------------------------
        // 5. 실제 값 업데이트 (데이터베이스 반영)
        // ------------------------------------------
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

        // 연관 관계 데이터 업데이트
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

        // 6. 변경 이력(History) 일괄 저장
        if (!histories.isEmpty()) {
            hospitalsHistoryJpaRepository.saveAll(histories);
        }

        return hospital.getId();
    }

    /**
     * 변경 여부를 확인하고 다를 경우 History 객체를 생성하여 리스트에 담는 Helper 메서드
     */
    private void compareAndAddHistory(List<HospitalHistory> histories, Hospital hospital, String subject, String before, String after, Users user) {
        String safeBefore = (before == null || before.equals("null")) ? "" : before.trim();
        String safeAfter = (after == null || after.equals("null")) ? "" : after.trim();

        if (!safeBefore.equals(safeAfter)) {
            histories.add(HospitalHistory.builder()
                    .hospital(hospital)
                    .users(user)
                    .modifySubject(subject)
                    .beforeModify(safeBefore)
                    .afterModify(safeAfter)
                    .build());
        }
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
