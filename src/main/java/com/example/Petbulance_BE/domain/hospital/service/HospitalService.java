package com.example.Petbulance_BE.domain.hospital.service;

import com.example.Petbulance_BE.domain.hospital.dto.*;
import com.example.Petbulance_BE.domain.hospital.dto.req.HospitalSearchReqDto;
import com.example.Petbulance_BE.domain.hospital.dto.res.*;
import com.example.Petbulance_BE.domain.hospital.entity.Hospital;
import com.example.Petbulance_BE.domain.hospital.repository.HospitalJpaRepository;
import com.example.Petbulance_BE.domain.hospitalWorktime.entity.HospitalWorktime;
import com.example.Petbulance_BE.domain.review.entity.UserReview;
import com.example.Petbulance_BE.global.common.error.exception.CustomException;
import com.example.Petbulance_BE.global.common.error.exception.ErrorCode;
import com.example.Petbulance_BE.global.common.type.AnimalType;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.stereotype.Service;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.*;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class HospitalService {

    private final HospitalJpaRepository hospitalRepository;

    public HospitalSearchResDto searchHospitalsProcess(HospitalSearchReqDto hospitalSearchReqDto) {

        // 1. DB에서 조회 (limit + 1로 한 개 더 가져와서 hasNext 체크)
        List<HospitalSearchDao> hospitalSearchDaos = hospitalRepository.searchHospitals(hospitalSearchReqDto);

        // 2. hasNext 처리
        boolean hasNext = hospitalSearchDaos.size() > hospitalSearchReqDto.getSize();
        if (hasNext) {
            hospitalSearchDaos = hospitalSearchDaos.subList(0, hospitalSearchReqDto.getSize());
        }

        DayOfWeek today = LocalDate.now().getDayOfWeek();
        String dayPrefix = today.toString().substring(0, 3).toLowerCase();
        LocalTime now = LocalTime.now();
        String[] dayOrder = {"mon", "tue", "wed", "thu", "fri", "sat", "sun"};
        int todayIndex = Arrays.asList(dayOrder).indexOf(dayPrefix);

        // 3. DTO 매핑
        List<HospitalsResDto> content = hospitalSearchDaos.stream()
                .map(hs -> {
                    boolean isOpenNow = false;
                    LocalTime openTimeToday = null;
                    LocalTime closeTimeToday = null;

                    // 오늘 영업시간 체크
                    switch (dayPrefix) {
                        case "mon" -> {
                            openTimeToday = hs.getMonOpenTime();
                            closeTimeToday = hs.getMonCloseTime();
                            isOpenNow = hs.getMonIsOpen() != null && hs.getMonIsOpen()
                                    && now.isAfter(openTimeToday) && now.isBefore(closeTimeToday);
                        }
                        case "tue" -> {
                            openTimeToday = hs.getTueOpenTime();
                            closeTimeToday = hs.getTueCloseTime();
                            isOpenNow = hs.getTueIsOpen() != null && hs.getTueIsOpen()
                                    && now.isAfter(openTimeToday) && now.isBefore(closeTimeToday);
                        }
                        case "wed" -> {
                            openTimeToday = hs.getWedOpenTime();
                            closeTimeToday = hs.getWedCloseTime();
                            isOpenNow = hs.getWedIsOpen() != null && hs.getWedIsOpen()
                                    && now.isAfter(openTimeToday) && now.isBefore(closeTimeToday);
                        }
                        case "thu" -> {
                            openTimeToday = hs.getThuOpenTime();
                            closeTimeToday = hs.getThuCloseTime();
                            isOpenNow = hs.getThuIsOpen() != null && hs.getThuIsOpen()
                                    && now.isAfter(openTimeToday) && now.isBefore(closeTimeToday);
                        }
                        case "fri" -> {
                            openTimeToday = hs.getFriOpenTime();
                            closeTimeToday = hs.getFriCloseTime();
                            isOpenNow = hs.getFriIsOpen() != null && hs.getFriIsOpen()
                                    && now.isAfter(openTimeToday) && now.isBefore(closeTimeToday);
                        }
                        case "sat" -> {
                            openTimeToday = hs.getSatOpenTime();
                            closeTimeToday = hs.getSatCloseTime();
                            isOpenNow = hs.getSatIsOpen() != null && hs.getSatIsOpen()
                                    && now.isAfter(openTimeToday) && now.isBefore(closeTimeToday);
                        }
                        case "sun" -> {
                            openTimeToday = hs.getSunOpenTime();
                            closeTimeToday = hs.getSunCloseTime();
                            isOpenNow = hs.getSunIsOpen() != null && hs.getSunIsOpen()
                                    && now.isAfter(openTimeToday) && now.isBefore(closeTimeToday);
                        }
                    }

                    // 다음 영업일 탐색
                    String openHours = null;
                    if (isOpenNow) {
                        openHours = String.format("%s ~ %s", openTimeToday, closeTimeToday);
                    } else {
                        LocalTime nextOpen = null;
                        LocalTime nextClose = null;
                        for (int i = 1; i <= 7; i++) {
                            int nextIndex = (todayIndex + i) % 7;
                            String nextDay = dayOrder[nextIndex];
                            boolean nextIsOpen = false;
                            switch (nextDay) {
                                case "mon" -> {
                                    nextOpen = hs.getMonOpenTime();
                                    nextClose = hs.getMonCloseTime();
                                    nextIsOpen = hs.getMonIsOpen() != null && hs.getMonIsOpen();
                                }
                                case "tue" -> {
                                    nextOpen = hs.getTueOpenTime();
                                    nextClose = hs.getTueCloseTime();
                                    nextIsOpen = hs.getTueIsOpen() != null && hs.getTueIsOpen();
                                }
                                case "wed" -> {
                                    nextOpen = hs.getWedOpenTime();
                                    nextClose = hs.getWedCloseTime();
                                    nextIsOpen = hs.getWedIsOpen() != null && hs.getWedIsOpen();
                                }
                                case "thu" -> {
                                    nextOpen = hs.getThuOpenTime();
                                    nextClose = hs.getThuCloseTime();
                                    nextIsOpen = hs.getThuIsOpen() != null && hs.getThuIsOpen();
                                }
                                case "fri" -> {
                                    nextOpen = hs.getFriOpenTime();
                                    nextClose = hs.getFriCloseTime();
                                    nextIsOpen = hs.getFriIsOpen() != null && hs.getFriIsOpen();
                                }
                                case "sat" -> {
                                    nextOpen = hs.getSatOpenTime();
                                    nextClose = hs.getSatCloseTime();
                                    nextIsOpen = hs.getSatIsOpen() != null && hs.getSatIsOpen();
                                }
                                case "sun" -> {
                                    nextOpen = hs.getSunOpenTime();
                                    nextClose = hs.getSunCloseTime();
                                    nextIsOpen = hs.getSunIsOpen() != null && hs.getSunIsOpen();
                                }
                            }
                            if (nextIsOpen) {
                                openHours = String.format("%s ~ %s", nextOpen, nextClose);
                                break;
                            }
                        }
                        if (nextOpen == null) openHours = null;
                    }

                    return HospitalsResDto.builder()
                            .hospitalId(hs.getId())
                            .name(hs.getName())
                            .lat(hs.getLat())
                            .lng(hs.getLng())
                            .distanceMeters(hs.getDistanceMeters())
                            .phone(hs.getPhoneNumber())
                            .types(hs.getTreatedAnimalTypes() != null ? hs.getTreatedAnimalTypes().split(",") : null)
                            .isOpenNow(isOpenNow)
                            .openHours(openHours)
                            .thumbnailUrl(hs.getUrl())
                            .rating(hs.getRating())
                            .reviewCount(hs.getReviewCount())
                            .build();
                })
                .collect(Collectors.toList());

        // 4. cursor 값 세팅
        Long cursorId = null;
        Double cursorDistance = null;
        Double cursorRating = null;
        Long cursorReviewCount = null;

        if (!hospitalSearchDaos.isEmpty()) {
            HospitalSearchDao last = hospitalSearchDaos.get(hospitalSearchDaos.size() - 1);

            // 정렬 기준에 따라 cursor 필드만 채움
            switch (hospitalSearchReqDto.getSortBy().toLowerCase()) {
                case "distance" -> {
                    cursorDistance = last.getDistanceMeters();
                    cursorId = last.getId();
                }
                case "rating" -> {
                    cursorRating = last.getRating()==null?0.0:last.getRating();
                    cursorId = last.getId();
                }
                case "reviewcount" -> {
                    cursorReviewCount = last.getReviewCount();
                    cursorId = last.getId();
                }
                default -> cursorId = last.getId();
            }
        }
        // 5. 결과 반환
        return HospitalSearchResDto.builder()
                .list(content)
                .hasNext(hasNext)
                .cursorId(cursorId)
                .cursorDistance(cursorDistance)
                .cursorRating(cursorRating)
                .cursorReviewCount(cursorReviewCount)
                .build();
    }


    public HospitalDetailResDto searchHospitalDetailProcess(Long hospitalId) {

        LocalTime now = LocalTime.now();
        DayOfWeek today = LocalDate.now().getDayOfWeek();
        String dayPrefix = today.toString().substring(0, 3).toUpperCase();

        Hospital hospital = hospitalRepository.findDetailHospital(hospitalId).orElseThrow(()->new CustomException(ErrorCode.NOT_FOUND_HOSPITAL));

        List<String> animalTypes = hospital.getTreatmentAnimals().stream().map(t ->
            t.getAnimaType().name()
        ).toList();

        Set<HospitalWorktime> hospitalWorktimes = hospital.getHospitalWorktimes();

        List<String> order = List.of("MON", "TUE", "WED", "THU", "FRI", "SAT", "SUN");

        Map<String, String> dayMap = Map.of(
                "MON", "월요일",
                "TUE", "화요일",
                "WED", "수요일",
                "THU", "목요일",
                "FRI", "금요일",
                "SAT", "토요일",
                "SUN", "일요일"
        );
        List<String> noteList = new LinkedList<>();

        AtomicReference<Boolean> openNow = new AtomicReference<>(false);

        List<OpenHours> openHoursList = hospitalWorktimes.stream()
                .distinct()
                .sorted(Comparator.comparing(hw ->
                        order.indexOf(hw.getId().getDayOfWeek())  // 요일 순서대로 정렬
                ))
                .map(hw -> {
                    OpenHours openHours = new OpenHours();
                    String dayOfWeek = hw.getId().getDayOfWeek();
                    openHours.setDay(dayOfWeek);

                    if(dayOfWeek.equals(dayPrefix)&& hw.getIsOpen() && now.isAfter(hw.getOpenTime()) && now.isBefore(hw.getCloseTime())) {
                        openNow.set(true);
                    }

                    if(hw.getIsOpen()){
                        openHours.setHours(hw.getOpenTime() + "-" + hw.getCloseTime());
                        return openHours;
                    }
                    openHours.setHours("CLOSED");
                    noteList.add(hw.getId().getDayOfWeek());
                    return openHours;
                })
                .toList();

        String note = "매주 " + noteList.stream().map(n->dayMap.get(n)).collect(Collectors.joining("/")) + " 휴무";

        return HospitalDetailResDto.builder()
                .hospitalId(hospitalId)
                .name(hospital.getName())
                .address(hospital.getAddress())
                .lat(hospital.getLat())
                .lng(hospital.getLng())
                .phone(hospital.getPhoneNumber())
                .acceptedAnimals(animalTypes)
                .openHours(openHoursList)
                .notes(note.length()==6?null:note)
                .openNow(openNow)
                .description(hospital.getInformation())
                .build();

    }

    public HospitalCardResDto searchHospitalCardProcess(Long hospitalId, Double lat, Double lng) {

        List<String> order = List.of("MON","TUE","WED","THU","FRI","SAT","SUN");

        // 1. 연관 데이터 포함 병원 조회 (한 번의 쿼리로 모든 연관 데이터 로드)
        Hospital hospital = hospitalRepository
                .findByIdWithDetails(hospitalId)
                .orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND_HOSPITAL));

        // 2. 거리 계산
        Double distance = hospitalRepository
                .calculateDistance(lng, lat, hospitalId)
                .orElse(0.0);

        // 3. 나머지 로직은 그대로
        List<String> list = hospital.getTreatmentAnimals().stream()
                .sorted((a, b) -> Long.compare(a.getId(), b.getId()))
                .map(t->t.getAnimaType().name())
                .toList();

        LocalTime now = LocalTime.now();
        DayOfWeek today = LocalDate.now().getDayOfWeek();
        String dayPrefix = today.toString().substring(0, 3).toUpperCase();

        List<HospitalWorktime> worktimes = hospital.getHospitalWorktimes().stream()
                .sorted(Comparator.comparing(hw -> order.indexOf(hw.getId().getDayOfWeek())))
                .toList();

        Optional<HospitalWorktime> todayWork = worktimes.stream()
                .filter(hw -> hw.getId().getDayOfWeek().equals(dayPrefix))
                .findFirst();

        boolean isOpen = false;
        String nextOpenHour = null;

        if(todayWork.isPresent()) {
            HospitalWorktime hw = todayWork.get();
            if(hw.getIsOpen()) {
                if(now.isAfter(hw.getOpenTime()) && now.isBefore(hw.getCloseTime())) {
                    isOpen = true;
                } else {
                    nextOpenHour = hw.getOpenTime() + " - " + hw.getCloseTime();
                }
            }
        }

        if(!isOpen) {
            int todayIndex = order.indexOf(dayPrefix);
            for(int i = 1; i <= 7; i++) {
                String nextDay = order.get((todayIndex + i) % 7);
                Optional<HospitalWorktime> nextWork = worktimes.stream()
                        .filter(hw -> hw.getId().getDayOfWeek().equals(nextDay) && hw.getIsOpen())
                        .findFirst();
                if(nextWork.isPresent()) {
                    HospitalWorktime hw = nextWork.get();
                    nextOpenHour = nextDay + " " + hw.getOpenTime() + " - " + hw.getCloseTime();
                    break;
                }
            }
        }

        Set<UserReview> userReviews = hospital.getUserReviews();
        Optional<Double> overallRating = hospitalRepository.getOverallRating(hospitalId);

        return HospitalCardResDto.builder()
                .hospitalId(hospital.getId())
                .name(hospital.getName())
                .lat(hospital.getLat())
                .lng(hospital.getLng())
                .distanceMeters(distance)
                .phone(hospital.getPhoneNumber())
                .types(list)
                .isOpenNow(isOpen)
                .nextOpenHours(nextOpenHour)
                .thumbnailUrl(hospital.getImage())
                .rating(overallRating.orElse(null))
                .reviewCount((long) userReviews.size())
                .build();
    }

    public List<HospitalMatchingResDto> hospitalMatching(String filter, AnimalType species, Double lat, Double lng) {

        LocalDate today = LocalDate.now(); // 현재 날짜
        LocalTime now = LocalTime.now(); // 현재 시간
        String s = null;
        if(species != null) {
            s = species.name();
        }

        return hospitalRepository.findMatchingHospitals(
                s,
                filter,
                lat,
                lng,
                today.getDayOfWeek(), // 현재 요일
                now // 현재 시간
        );
    }

    public DetailHospitalResDto detailHospital(Long hospitalId, Double lat, Double lng) {
        return hospitalRepository.findHospitalDetail(hospitalId, lat, lng);

    }
}
