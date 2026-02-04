package com.example.Petbulance_BE.domain.hospital.repository;

import com.example.Petbulance_BE.domain.hospital.dto.HospitalSearchDto;
import com.example.Petbulance_BE.domain.hospital.dto.req.HospitalSearchReqDto;
import com.example.Petbulance_BE.domain.hospital.dto.res.DetailHospitalResDto;
import com.example.Petbulance_BE.domain.hospital.dto.res.HospitalMatchingResDto;
import com.example.Petbulance_BE.domain.hospital.entity.QHospital;
import com.example.Petbulance_BE.domain.hospital.entity.QTag;
import com.example.Petbulance_BE.domain.hospitalWorktime.entity.HospitalWorktime;
import com.example.Petbulance_BE.domain.hospitalWorktime.entity.QHospitalWorktime;
import com.example.Petbulance_BE.domain.treatmentAnimal.entity.QTreatmentAnimal;
import com.example.Petbulance_BE.domain.treatmentAnimal.entity.TreatmentAnimal;
import com.example.Petbulance_BE.global.common.type.AnimalType;
import com.example.Petbulance_BE.global.common.type.DetailAnimalType;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.Tuple;
import com.querydsl.core.types.Expression;
import com.querydsl.core.types.ExpressionUtils;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.*;
import com.querydsl.jpa.JPAExpressions;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import org.springframework.util.StringUtils;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.*;
import java.util.stream.Collectors;

import static com.example.Petbulance_BE.domain.hospital.entity.QHospital.hospital;
import static com.example.Petbulance_BE.domain.hospitalWorktime.entity.QHospitalWorktime.hospitalWorktime;
import static com.example.Petbulance_BE.domain.review.entity.QUserReview.userReview;
import static com.example.Petbulance_BE.domain.treatmentAnimal.entity.QTreatmentAnimal.treatmentAnimal;
import static com.example.Petbulance_BE.domain.treatmentAnimal.entity.QMajorTreatMentAnimal.majorTreatMentAnimal;
import static com.example.Petbulance_BE.domain.hospital.entity.QTag.tag1;
import static com.querydsl.core.types.dsl.MathExpressions.*;

@Repository
@RequiredArgsConstructor
public class HospitalRepositoryCustomImpl implements HospitalRepositoryCustom {

    private static final Map<String, Integer> DAY_OF_WEEK_MAP = new HashMap<>();

    static {
        DAY_OF_WEEK_MAP.put("MON", 1);
        DAY_OF_WEEK_MAP.put("TUE", 2);
        DAY_OF_WEEK_MAP.put("WED", 3);
        DAY_OF_WEEK_MAP.put("THU", 4);
        DAY_OF_WEEK_MAP.put("FRI", 5);
        DAY_OF_WEEK_MAP.put("SAT", 6);
        DAY_OF_WEEK_MAP.put("SUN", 7);
    }

    private final JPAQueryFactory queryFactory;

    @Override
    public List<HospitalSearchDto> searchHospitals(HospitalSearchReqDto dto) {
        // 1. 기초 데이터 및 환경 변수 설정
        DayOfWeek today = LocalDate.now().getDayOfWeek();
        String todayStr = today.toString().substring(0, 3).toUpperCase();
        LocalTime now = LocalTime.now();

        // 거리 계산 식 (Null Safe)
        NumberExpression<Double> doubleNumberExpression = calculateDistance(dto.getLat(), dto.getLng());
        NumberExpression<Double> safeDistance =
                doubleNumberExpression != null ? doubleNumberExpression.coalesce(0.0) : Expressions.asNumber(0.0);

        // 2. 메인 쿼리: 검색 및 정렬 (무거운 CASE WHEN 제거)
        JPAQuery<HospitalSearchDto> query = queryFactory.select(
                        Projections.fields(HospitalSearchDto.class,
                                hospital.id.as("id"),
                                hospital.name.as("name"),
                                hospital.lat.as("lat"),
                                hospital.lng.as("lng"),
                                hospital.phoneNumber.as("phoneNumber"),
                                hospital.url.as("url"),
                                Expressions.asNumber(Expressions.constant(0.0)).as("distanceMeters"),
                                getReviewCountSub().as("reviewCount"),
                                getRatingSub().as("rating"),
                                hospital.image.as("image")
                        )
                )
                .from(hospital)
                .where(
                        likeQ(dto.getQ()),
                        likeRegion(dto.getRegion()),
                        withinBounds(dto.getBounds()),
                        filterByAnimalArray(dto.getAnimalArray()),
                        getBooleanExpression(dto.getOpenNow(), todayStr, now)
                );

        // 3. 정렬 및 커서 페이징 처리 (별도 메소드로 분리)
        applySortingAndCursor(query, dto, safeDistance);

        // 4. 리스트 조회 (Size + 1)
        List<HospitalSearchDto> results = query.offset(0).limit(dto.getSize() + 1).fetch();

        if (results.isEmpty()) return results;

        // 5. 지연 채우기 (Post-Fetching)
        // 조회된 ID 리스트에 대해서만 추가 정보를 채움
        List<Long> hospitalIds = results.stream().map(HospitalSearchDto::getId).toList();
        fillWeeklyWorktimes(results, hospitalIds); // 7일치 운영시간 매핑
        fillAnimalTypes(results, hospitalIds);     // 동물종 매핑

        return results;
    }

    //1 정렬 및 커서 페이징
    private void applySortingAndCursor(JPAQuery<HospitalSearchDto> query, HospitalSearchReqDto dto, NumberExpression<Double> safeDistance) {
        String sortBy = dto.getSortBy();
        Long cId = dto.getCursorId();
        NumberPath<Long> reviewCountPath = Expressions.numberPath(Long.class, "reviewCount");
        NumberPath<Double> ratingPath = Expressions.numberPath(Double.class, "rating");

        if ("distance".equalsIgnoreCase(sortBy)) {
            if (dto.getCursorDistance() != null && cId != null) {
                query.where(safeDistance.gt(dto.getCursorDistance())
                        .or(safeDistance.eq(dto.getCursorDistance()).and(hospital.id.gt(cId))));
            }
            query.orderBy(safeDistance.asc(), hospital.id.asc());
        }
        else if ("rating".equalsIgnoreCase(sortBy)) {
            if (dto.getCursorRating() != null && cId != null) {
                query.having(ratingPath.lt(dto.getCursorRating())
                        .or(ratingPath.eq(dto.getCursorRating()).and(hospital.id.gt(cId))));
            }
            query.orderBy(ratingPath.desc(), hospital.id.asc());
        }
        else if ("reviewCount".equalsIgnoreCase(sortBy)) {
            if (dto.getCursorReviewCount() != null && cId != null) {
                query.having(reviewCountPath.lt(dto.getCursorReviewCount())
                        .or(reviewCountPath.eq(dto.getCursorReviewCount()).and(hospital.id.gt(cId))));
            }
            query.orderBy(reviewCountPath.desc(), hospital.id.asc());
        }
        else {
            if (cId != null) query.where(hospital.id.gt(cId));
            query.orderBy(hospital.id.asc());
        }
    }

    //2 7일치 운영시간 채우기
    private void fillWeeklyWorktimes(List<HospitalSearchDto> results, List<Long> ids) {
        List<HospitalWorktime> worktimes = queryFactory
                .selectFrom(hospitalWorktime)
                .where(hospitalWorktime.hospital.id.in(ids))
                .fetch();

        Map<Long, List<HospitalWorktime>> worktimeMap = worktimes.stream()
                .collect(Collectors.groupingBy(w -> w.getHospital().getId()));

        results.forEach(dto -> {
            List<HospitalWorktime> times = worktimeMap.getOrDefault(dto.getId(), Collections.emptyList());
            for (HospitalWorktime wt : times) {
                assignDayTime(dto, wt); // 요일별로 필드 매핑
            }
        });
    }

    private void assignDayTime(HospitalSearchDto dto, HospitalWorktime wt) {
        String day = wt.getId().getDayOfWeek();
        switch (day) {
            case "MON" -> {
                dto.setMonOpenTime(wt.getOpenTime()); dto.setMonCloseTime(wt.getCloseTime());
                dto.setMonBreakStartTime(wt.getBreakStartTime()); dto.setMonBreakEndTime(wt.getBreakEndTime());
                dto.setMonReceptionDeadline(wt.getReceptionDeadline()); dto.setMonIsOpen(wt.getIsOpen());
            }
            case "TUE" -> {
                dto.setTueOpenTime(wt.getOpenTime()); dto.setTueCloseTime(wt.getCloseTime());
                dto.setTueBreakStartTime(wt.getBreakStartTime()); dto.setTueBreakEndTime(wt.getBreakEndTime());
                dto.setTueReceptionDeadline(wt.getReceptionDeadline()); dto.setTueIsOpen(wt.getIsOpen());
            }
            case "WED" -> {
                dto.setWedOpenTime(wt.getOpenTime()); dto.setWedCloseTime(wt.getCloseTime());
                dto.setWedBreakStartTime(wt.getBreakStartTime()); dto.setWedBreakEndTime(wt.getBreakEndTime());
                dto.setWedReceptionDeadline(wt.getReceptionDeadline()); dto.setWedIsOpen(wt.getIsOpen());
            }
            case "THU" -> {
                dto.setThuOpenTime(wt.getOpenTime()); dto.setThuCloseTime(wt.getCloseTime());
                dto.setThuBreakStartTime(wt.getBreakStartTime()); dto.setThuBreakEndTime(wt.getBreakEndTime());
                dto.setThuReceptionDeadline(wt.getReceptionDeadline()); dto.setThuIsOpen(wt.getIsOpen());
            }
            case "FRI" -> {
                dto.setFriOpenTime(wt.getOpenTime()); dto.setFriCloseTime(wt.getCloseTime());
                dto.setFriBreakStartTime(wt.getBreakStartTime()); dto.setFriBreakEndTime(wt.getBreakEndTime());
                dto.setFriReceptionDeadline(wt.getReceptionDeadline()); dto.setFriIsOpen(wt.getIsOpen());
            }
            case "SAT" -> {
                dto.setSatOpenTime(wt.getOpenTime()); dto.setSatCloseTime(wt.getCloseTime());
                dto.setSatBreakStartTime(wt.getBreakStartTime()); dto.setSatBreakEndTime(wt.getBreakEndTime());
                dto.setSatReceptionDeadline(wt.getReceptionDeadline()); dto.setSatIsOpen(wt.getIsOpen());
            }
            case "SUN" -> {
                dto.setSunOpenTime(wt.getOpenTime()); dto.setSunCloseTime(wt.getCloseTime());
                dto.setSunBreakStartTime(wt.getBreakStartTime()); dto.setSunBreakEndTime(wt.getBreakEndTime());
                dto.setSunReceptionDeadline(wt.getReceptionDeadline()); dto.setSunIsOpen(wt.getIsOpen());
            }
        }
    }

    //3 동물 타입 채우기
    private void fillAnimalTypes(List<HospitalSearchDto> results, List<Long> ids) {
        List<Tuple> types = queryFactory
                .select(treatmentAnimal.hospital.id, treatmentAnimal.animalType)
                .from(treatmentAnimal)
                .where(treatmentAnimal.hospital.id.in(ids))
                .fetch();

        Map<Long, String> typeMap = types.stream()
                .collect(Collectors.groupingBy(
                        t -> t.get(treatmentAnimal.hospital.id),
                        Collectors.mapping(t -> t.get(treatmentAnimal.animalType).toString(), Collectors.joining(","))
                ));

        results.forEach(dto -> dto.setTreatedAnimalTypes(typeMap.get(dto.getId())));
    }

    private NumberExpression<Long> getReviewCountSub() {
        return Expressions.asNumber(JPAExpressions.select(userReview.count()).from(userReview).where(userReview.hospital.eq(hospital))).longValue().coalesce(0L);
    }

    private NumberExpression<Double> getRatingSub() {
        return Expressions.asNumber(JPAExpressions.select(userReview.overallRating.avg()).from(userReview).where(userReview.hospital.eq(hospital))).doubleValue().coalesce(0.0);
    }

    private BooleanExpression getBooleanExpression(Boolean openNow, String todayStr, LocalTime now) {
        if (!Boolean.TRUE.equals(openNow)) return null;
        return hospital.id.in(
                JPAExpressions.select(hospitalWorktime.hospital.id)
                        .from(hospitalWorktime)
                        .where(hospitalWorktime.id.dayOfWeek.eq(todayStr)
                                .and(hospitalWorktime.isOpen.isTrue())
                                .and(hospitalWorktime.openTime.loe(now))
                                .and(hospitalWorktime.closeTime.goe(now)))
        );
    }

    private BooleanExpression likeQ(String q) {
        return StringUtils.hasText(q) ? hospital.name.contains(q) : null;
    }

    private BooleanExpression likeRegion(String region) {
        if (!StringUtils.hasText(region)) return null;

        String[] regions = region.split(",");
        BooleanBuilder builder = new BooleanBuilder();

        StringTemplate addressNoSpace = Expressions.stringTemplate("REPLACE({0}, ' ', '')", hospital.address);
        for (String r : regions) {
            String trimmedRegion = r.trim();
            if (StringUtils.hasText(trimmedRegion)) {
                builder.or(addressNoSpace.like(trimmedRegion + "%"));
            }
        }
        return Expressions.asBoolean(builder.getValue());
    }

    private BooleanExpression filterByAnimalArray(String[] animalArray) {
        if (animalArray == null || animalArray.length == 0) return null;
        return hospital.id.in(
                JPAExpressions.select(majorTreatMentAnimal.hospital.id)
                        .from(majorTreatMentAnimal)
                        .where(majorTreatMentAnimal.animalType.stringValue().in(animalArray))
        );
    }

    private BooleanExpression withinBounds(Double[] bounds) {
        if (bounds == null || bounds.length != 4) return null;
        return hospital.lat.between(bounds[0], bounds[2]).and(hospital.lng.between(bounds[1], bounds[3]));
    }

    private NumberExpression<Double> calculateDistance(Double lat, Double lng) {
        if (lat == null || lng == null) return null;
        return Expressions.numberTemplate(Double.class, "ST_Distance_Sphere({0}, ST_GeomFromText({1}, 4326))",
                hospital.location, "POINT(" + lat + " " + lng + ")");
    }

    @Override
    public List<HospitalMatchingResDto> findMatchingHospitals(
            String species,
            String filter,
            Double lat,
            Double lng,
            DayOfWeek today,
            LocalTime now
    ) {

        QHospital hospital = QHospital.hospital;
        QHospitalWorktime work = QHospitalWorktime.hospitalWorktime;
        QTreatmentAnimal treat = QTreatmentAnimal.treatmentAnimal;
        QTag tag = QTag.tag1;

        // -----------------------------------
        // 기본 준비
        // -----------------------------------
        String todayStr = today.toString().substring(0, 3).toUpperCase();
        NumberExpression<Double> distance = distanceExpression(lat, lng);

//        BooleanExpression speciesFilter =
//                treat.animaType.eq(AnimalType.valueOf(species));

        // -----------------------------
        // 오늘 영업 요일인지 체크
        // -----------------------------
        NumberExpression<Integer> isOpenTodayCase =
                Expressions.numberTemplate(Integer.class,
                        "MAX(CASE WHEN {0} = {1} AND {2} = true THEN 1 ELSE 0 END)",
                        work.id.dayOfWeek,
                        Expressions.constant(todayStr),
                        work.isOpen
                );

        // -----------------------------
        // 현재 시간 기준으로 OPEN 여부 계산
        // -----------------------------
        NumberExpression<Integer> isOpenNowCase =
                Expressions.numberTemplate(Integer.class,
                        "MAX(CASE " +
                                "WHEN {0} = true " +
                                "AND ( " +
                                "     ({1} <= {2} AND {3} BETWEEN {1} AND {2}) " +
                                "  OR ({1} > {2} AND ({3} >= {1} OR {3} <= {2})) " +
                                ") " +
                                "AND ( {4} IS NULL OR NOT({3} BETWEEN {4} AND {5}) ) " +
                                "THEN 1 ELSE 0 END)",
                        work.isOpen,
                        work.openTime,
                        work.closeTime,
                        Expressions.constant(now),
                        work.breakStartTime,
                        work.breakEndTime
                );

        // -----------------------------
        // SELECT용 isOpenNow(Boolean)
        // -----------------------------
        Expression<Boolean> isOpenNowExpr =
                Expressions.booleanTemplate(
                        "({0} = 1 AND {1} = 1)",
                        isOpenTodayCase,
                        isOpenNowCase
                );

        // -----------------------------
        // HAVING 필터전용 CASE (숫자 1개만 반환)
        // -----------------------------
        NumberExpression<Integer> isOpenNowFilter =
                Expressions.numberTemplate(Integer.class,
                        "CASE WHEN ({0} = 1 AND {1} = 1) THEN 1 ELSE 0 END",
                        isOpenTodayCase,
                        isOpenNowCase
                );

        // -----------------------------
        // 오늘 closeTime 계산
        // -----------------------------
        Expression<LocalTime> todayCloseTimeExpr =
                Expressions.timeTemplate(LocalTime.class,
                        "MAX(CASE WHEN {0} = {1} THEN {2} END)",
                        work.id.dayOfWeek,
                        Expressions.constant(todayStr),
                        work.closeTime
                );

        BooleanExpression speciesCheck= null;

        if(species != null){
            speciesCheck =  treat.animalType.eq(DetailAnimalType.valueOf(species));
        }

        // -----------------------------
        // 기본 SELECT
        // -----------------------------
        JPAQuery<HospitalMatchingResDto> query = queryFactory
                .select(
                        Projections.constructor(
                                HospitalMatchingResDto.class,
                                hospital.id,
                                hospital.image,
                                hospital.name,
                                isOpenNowExpr,
                                distance,
                                todayCloseTimeExpr,
                                hospital.phoneNumber
                        )
                )
                .from(hospital)
                .join(hospital.treatmentAnimals, treat)
                .leftJoin(hospital.hospitalWorktimes, work)
                .join(hospital.tags, tag)
                .where(speciesCheck)
                .groupBy(hospital.id);

        // -----------------------------
        // 필터 적용
        // -----------------------------
        if ("IS_OPEN_NOW".equals(filter)) {
            query.having(isOpenNowFilter.eq(1));   // 현재 영업중인 병원만
        }else if("TWENTY_FOUR_HOUR".equals(filter)){
            query.where(tag.tag.eq("24시간"));
        }

        // -----------------------------
        // 정렬 + LIMIT
        // -----------------------------
        List<HospitalMatchingResDto> result = query
                .orderBy(distance.asc())
                .limit(3)
                .fetch();

        if (result.isEmpty()) return result;

        // -----------------------------
        // 동물 및 태그 조회
        // -----------------------------
        List<Long> hospitalIds = result.stream()
                .map(HospitalMatchingResDto::getHospitalId)
                .toList();

        List<TreatmentAnimal> animals = queryFactory
                .selectFrom(treat)
                .where(treat.hospital.id.in(hospitalIds))
                .fetch();

        Map<Long, List<String>> animalMap = animals.stream()
                .collect(Collectors.groupingBy(
                        ta -> ta.getHospital().getId(),
                        Collectors.mapping(
                                ta -> ta.getAnimalType().getDescription(),
                                Collectors.toList()
                        )
                ));

        Map<Long, List<String>> tagMap = queryFactory
                .select(tag.hospital.id, tag.tag)
                .from(tag)
                .where(tag.hospital.id.in(hospitalIds))
                .fetch()
                .stream()
                .collect(Collectors.groupingBy(
                        tuple -> tuple.get(tag.hospital.id),
                        Collectors.mapping(
                                tuple -> tuple.get(tag.tag),
                                Collectors.toList()
                        )
                ));

        // -----------------------------
        // DTO 매핑
        // -----------------------------
        result.forEach(res -> {
            res.setTreatableAnimals(
                    animalMap.getOrDefault(res.getHospitalId(), new ArrayList<>())
            );
            res.setTags(tagMap.get(res.getHospitalId()));
        });

        return result;
    }



    private BooleanExpression getFilterExpression(
            String filter,
            BooleanExpression openNowExpr
    ) {
        QHospital h = QHospital.hospital;

        return switch (filter) {
            case "DISTANCE" -> Expressions.TRUE;
            case "TWENTY_FOUR_HOUR" -> h.twentyFourHours.eq(true);
            case "IS_OPEN_NOW" -> openNowExpr;
            default -> Expressions.TRUE;
        };
    }

    private NumberExpression<Double> distanceExpression(Double lat, Double lng) {

        QHospital h = QHospital.hospital;

        return acos(
                cos(radians(Expressions.constant(lat)))
                        .multiply(cos(radians(h.lat)))
                        .multiply(
                                cos(
                                        radians(h.lng)
                                                .subtract(radians(Expressions.constant(lng)))
                                )
                        )
                        .add(
                                sin(radians(Expressions.constant(lat)))
                                        .multiply(sin(radians(h.lat)))
                        )
        ).multiply(6371);
    }

    public DetailHospitalResDto findHospitalDetail(Long hospitalId, Double userLat, Double userLng) {

        List<String> order = List.of("MON", "TUE", "WED", "THU", "FRI", "SAT", "SUN");

        QHospital h = QHospital.hospital;
        QHospitalWorktime w = hospitalWorktime;
        QTreatmentAnimal t = QTreatmentAnimal.treatmentAnimal;

        NumberExpression<Double> distanceExp = distanceExpression(userLat, userLng);

        // ======================
        // 1) 병원 기본 정보 조회
        // ======================
        Tuple base = queryFactory
                .select(
                        h.id,
                        h.name,
                        h.phoneNumber,
                        h.address,
                        h.lat,
                        h.lng,
                        distanceExp
                )
                .from(h)
                .where(h.id.eq(hospitalId))
                .fetchOne();

        if (base == null) return null;

        Long id = base.get(h.id);
        String name = base.get(h.name);
        String phone = base.get(h.phoneNumber);
        String address = base.get(h.address);
        Double lat = base.get(h.lat);
        Double lng = base.get(h.lng);
        Double distance = base.get(distanceExp);

        // ======================
        // 2) 치료 가능 동물 조회
        // ======================
        List<String> acceptedAnimals = queryFactory
                .select(t.animalType)
                .from(t)
                .where(t.hospital.id.eq(hospitalId))
                .fetch()
                .stream()
                .map(DetailAnimalType::getDescription)
                .collect(Collectors.toList());

        // ======================
        // 3) 요일별 worktime 조회
        // ======================
        List<HospitalWorktime> weekly = queryFactory
                .select(w)
                .from(w)
                .where(w.hospital.id.eq(hospitalId))
                .fetch();

        List<HospitalWorktime> list = weekly.stream().sorted(Comparator.comparing(hw ->
                order.indexOf(hw.getId().getDayOfWeek())
        )).toList();

        // 오늘 요일
        DayOfWeek today = LocalDate.now().getDayOfWeek();
        LocalTime now = LocalTime.now();

        // 🚨 수정된 부분 1: todayWork 필터링 로직 수정 (NumberFormatException 발생 지점)
        // DB 저장값("FRI")을 Map을 사용하여 정수로 변환하여 비교
        HospitalWorktime todayWork = list.stream()
                .filter(x -> {
                    String dayStr = x.getId().getDayOfWeek().toUpperCase();
                    Integer dayInt = DAY_OF_WEEK_MAP.get(dayStr);
                    // 유효한 요일이고, 오늘 요일과 일치하는지 확인
                    return dayInt != null && dayInt.equals(today.getValue());
                })
                .findFirst()
                .orElse(null);

        boolean openNow = false;
        LocalTime todayCloseTime = null;

        if (todayWork != null && Boolean.TRUE.equals(todayWork.getIsOpen())) {

            LocalTime open = todayWork.getOpenTime();
            LocalTime close = todayWork.getCloseTime();

            // ---- 24시간 영업 처리 (00:00 ~ 23:59 또는 23:59:59 등)
            boolean is24Hours =
                    open.equals(LocalTime.MIDNIGHT) &&
                            (close.equals(LocalTime.MAX) || close.equals(LocalTime.of(23,59)) || close.equals(LocalTime.of(23,59,59)));

            if (is24Hours) {
                openNow = true;
            } else {

                // ---- 자정 넘기는 영업 처리
                boolean isOvernight = close.isBefore(open);
                boolean inBusinessHours;

                if (isOvernight) {
                    // 예: 18:00 ~ 02:00
                    inBusinessHours = now.isAfter(open) || now.isBefore(close);
                } else {
                    // 일반 케이스
                    inBusinessHours = !now.isBefore(open) && !now.isAfter(close);
                }

                // ---- 휴게시간 처리
                boolean inBreak = false;
                if (todayWork.getBreakStartTime() != null && todayWork.getBreakEndTime() != null) {
                    LocalTime bStart = todayWork.getBreakStartTime();
                    LocalTime bEnd = todayWork.getBreakEndTime();

                    boolean breakOvernight = bEnd.isBefore(bStart);

                    if (breakOvernight) {
                        inBreak = now.isAfter(bStart) || now.isBefore(bEnd);
                    } else {
                        inBreak = !now.isBefore(bStart) && !now.isAfter(bEnd);
                    }
                }

                openNow = inBusinessHours && !inBreak;
            }

            todayCloseTime = close;
        }

        // ======================
        // 4) openHours 변환
        // ======================
        List<DetailHospitalResDto.OpenHour> openHours =
                list.stream()
                        .map(work -> {
                            String hours;
                            if (!Boolean.TRUE.equals(work.getIsOpen())) {
                                hours = "휴진";
                            } else {
                                hours = work.getOpenTime() + "-" + work.getCloseTime();
                            }

                            // 🚨 수정된 부분 2: openHours 생성 로직 수정 (NumberFormatException 발생 지점)
                            // Map을 사용하여 문자열 요일을 정수로 변환하여 convertDay에 전달
                            Integer dayInt = DAY_OF_WEEK_MAP.get(work.getId().getDayOfWeek().toUpperCase());

                            // 매핑 실패 시(null) 예외 처리 또는 기본값 처리 로직을 추가하는 것이 좋습니다.
                            if (dayInt == null) {
                                // 예외를 던져 문제 있는 데이터를 확인하도록 유도합니다.
                                throw new IllegalStateException("Invalid day of week key found: " + work.getId().getDayOfWeek());
                            }

                            return new DetailHospitalResDto.OpenHour(
                                    convertDay(dayInt),
                                    hours
                            );
                        })
                        .collect(Collectors.toList());

        // ======================
        // 5) DTO 빌드
        // ======================
        return DetailHospitalResDto.builder()
                .hospitalId(id)
                .name(name)
                .phone(phone)
                .reviewAvg(4.8)       // 임시 상수값
                .reviewCount(234)     // 임시 상수값
                .openNow(openNow)
                .todayCloseTime(todayCloseTime)
                .distanceKm(distance)
                .acceptedAnimals(acceptedAnimals)
                .location(new DetailHospitalResDto.Location(
                        address,
                        lat,
                        lng
                ))
                .openHours(openHours)
                .build();
    }

    private String convertDay(int dayOfWeek) {
        return switch (dayOfWeek) {
            case 1 -> "MON";
            case 2 -> "TUE";
            case 3 -> "WED";
            case 4 -> "THU";
            case 5 -> "FRI";
            case 6 -> "SAT";
            case 7 -> "SUN";
            default -> "UNKNOWN";
        };
    }

}