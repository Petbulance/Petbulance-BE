package com.example.Petbulance_BE.domain.hospital.repository;

import com.example.Petbulance_BE.domain.hospital.dto.HospitalSearchDao;
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
import com.querydsl.core.Tuple;
import com.querydsl.core.types.Expression;
import com.querydsl.core.types.ExpressionUtils;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.core.types.dsl.NumberExpression;
import com.querydsl.jpa.JPAExpressions;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import org.springframework.util.StringUtils;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static com.example.Petbulance_BE.domain.hospital.entity.QHospital.hospital;
import static com.example.Petbulance_BE.domain.hospitalWorktime.entity.QHospitalWorktime.hospitalWorktime;
import static com.example.Petbulance_BE.domain.review.entity.QUserReview.userReview;
import static com.example.Petbulance_BE.domain.treatmentAnimal.entity.QTreatmentAnimal.treatmentAnimal;
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
    public List<HospitalSearchDao> searchHospitals(HospitalSearchReqDto dto) {
        String q = dto.getQ(); //병원 검색어
        String region = dto.getRegion(); //지역 검색어 ex)서울특별시강남구, 서울특별시
        Double lat = dto.getLat(); //위도 37.1
        Double lng = dto.getLng(); //경도 16.5
        Double[] bounds = dto.getBounds(); //minLat,minLng,maxLat,maxLng
        String[] animalArray = dto.getAnimalArray(); //동물종 ['FISH', 'BIRDS']
        Boolean openNow = dto.getOpenNow(); //현재 운영중인 곳만 true

        DayOfWeek today = LocalDate.now().getDayOfWeek();
        String todayStr = today.toString().substring(0, 3).toUpperCase();
        LocalTime now = LocalTime.now();

        BooleanExpression openNowFilter = getBooleanExpression(openNow, todayStr, now);

        NumberExpression<Double> doubleNumberExpression = calculateDistance(lat, lng);

        //결과가 쿼리가 반환되어 null은 아니지만 db에서 누락, 잘못된 값이 있을때 쿼리의 결과가 null이 나올 수 있음 그래서 .coalesce(0.0)
        //아예 lat,lng가 존재하지 않으면 쿼리가 아닌 null이 반환 그럴때 Expressions.asNumber(0.0);
        NumberExpression<Double> safeDistance =
                doubleNumberExpression != null ? doubleNumberExpression.coalesce(0.0) : Expressions.asNumber(0.0);


        JPAQuery<HospitalSearchDao> query = queryFactory.select(
                        Projections.fields(HospitalSearchDao.class,
                                hospital.id.as("id"),
                                hospital.name.as("name"),
                                hospital.lat.as("lat"),
                                hospital.lng.as("lng"),
                                hospital.phoneNumber.as("phoneNumber"),
                                hospital.url.as("url"),
                                doubleNumberExpression != null ? doubleNumberExpression.as("distanceMeters")  //numberTemplate기반
                                        : ExpressionUtils.as(Expressions.constant(0.0), "distanceMeters"),
                                Expressions.stringTemplate(
                                        "group_concat(DISTINCT {0})",
                                        treatmentAnimal.animaType.stringValue()
                                ).as("treatedAnimalTypes"),
                                userReview.id.count().as( "reviewCount"),
                                userReview.overallRating.avg().as("rating"),
                                // 월요일
                                Expressions.timeTemplate(LocalTime.class,
                                        "MAX(CASE WHEN {0} = {1} THEN {2} END)",
                                        hospitalWorktime.id.dayOfWeek, "MON", hospitalWorktime.openTime
                                ).as("monOpenTime"),
                                Expressions.timeTemplate(LocalTime.class,
                                        "MAX(CASE WHEN {0} = {1} THEN {2} END)",
                                        hospitalWorktime.id.dayOfWeek, "MON", hospitalWorktime.closeTime
                                ).as("monCloseTime"),
                                Expressions.timeTemplate(LocalTime.class,
                                        "MAX(CASE WHEN {0} = {1} THEN {2} END)",
                                        hospitalWorktime.id.dayOfWeek, "MON", hospitalWorktime.breakStartTime
                                ).as("monBreakStartTime"),
                                Expressions.timeTemplate(LocalTime.class,
                                        "MAX(CASE WHEN {0} = {1} THEN {2} END)",
                                        hospitalWorktime.id.dayOfWeek, "MON", hospitalWorktime.breakEndTime
                                ).as("monBreakEndTime"),
                                Expressions.timeTemplate(LocalTime.class,
                                        "MAX(CASE WHEN {0} = {1} THEN {2} END)",
                                        hospitalWorktime.id.dayOfWeek, "MON", hospitalWorktime.receptionDeadline
                                ).as("monReceptionDeadline"),
                                Expressions.booleanTemplate(
                                        "MAX(CASE WHEN {0} = {1} THEN {2} END)",
                                        hospitalWorktime.id.dayOfWeek, "MON", hospitalWorktime.isOpen
                                ).as("monIsOpen"),

                                // 화요일
                                Expressions.timeTemplate(LocalTime.class,
                                        "MAX(CASE WHEN {0} = {1} THEN {2} END)",
                                        hospitalWorktime.id.dayOfWeek, "TUE", hospitalWorktime.openTime
                                ).as("tueOpenTime"),
                                Expressions.timeTemplate(LocalTime.class,
                                        "MAX(CASE WHEN {0} = {1} THEN {2} END)",
                                        hospitalWorktime.id.dayOfWeek, "TUE", hospitalWorktime.closeTime
                                ).as("tueCloseTime"),
                                Expressions.timeTemplate(LocalTime.class,
                                        "MAX(CASE WHEN {0} = {1} THEN {2} END)",
                                        hospitalWorktime.id.dayOfWeek, "TUE", hospitalWorktime.breakStartTime
                                ).as("tueBreakStartTime"),
                                Expressions.timeTemplate(LocalTime.class,
                                        "MAX(CASE WHEN {0} = {1} THEN {2} END)",
                                        hospitalWorktime.id.dayOfWeek, "TUE", hospitalWorktime.breakEndTime
                                ).as("tueBreakEndTime"),
                                Expressions.timeTemplate(LocalTime.class,
                                        "MAX(CASE WHEN {0} = {1} THEN {2} END)",
                                        hospitalWorktime.id.dayOfWeek, "TUE", hospitalWorktime.receptionDeadline
                                ).as("tueReceptionDeadline"),
                                Expressions.booleanTemplate(
                                        "MAX(CASE WHEN {0} = {1} THEN {2} END)",
                                        hospitalWorktime.id.dayOfWeek, "TUE", hospitalWorktime.isOpen
                                ).as("tueIsOpen"),

                                // 수요일
                                Expressions.timeTemplate(LocalTime.class,
                                        "MAX(CASE WHEN {0} = {1} THEN {2} END)",
                                        hospitalWorktime.id.dayOfWeek, "WED", hospitalWorktime.openTime
                                ).as("wedOpenTime"),
                                Expressions.timeTemplate(LocalTime.class,
                                        "MAX(CASE WHEN {0} = {1} THEN {2} END)",
                                        hospitalWorktime.id.dayOfWeek, "WED", hospitalWorktime.closeTime
                                ).as("wedCloseTime"),
                                Expressions.timeTemplate(LocalTime.class,
                                        "MAX(CASE WHEN {0} = {1} THEN {2} END)",
                                        hospitalWorktime.id.dayOfWeek, "WED", hospitalWorktime.breakStartTime
                                ).as("wedBreakStartTime"),
                                Expressions.timeTemplate(LocalTime.class,
                                        "MAX(CASE WHEN {0} = {1} THEN {2} END)",
                                        hospitalWorktime.id.dayOfWeek, "WED", hospitalWorktime.breakEndTime
                                ).as("wedBreakEndTime"),
                                Expressions.timeTemplate(LocalTime.class,
                                        "MAX(CASE WHEN {0} = {1} THEN {2} END)",
                                        hospitalWorktime.id.dayOfWeek, "WED", hospitalWorktime.receptionDeadline
                                ).as("wedReceptionDeadline"),
                                Expressions.booleanTemplate(
                                        "MAX(CASE WHEN {0} = {1} THEN {2} END)",
                                        hospitalWorktime.id.dayOfWeek, "WED", hospitalWorktime.isOpen
                                ).as("wedIsOpen"),

                                // 목요일
                                Expressions.timeTemplate(LocalTime.class,
                                        "MAX(CASE WHEN {0} = {1} THEN {2} END)",
                                        hospitalWorktime.id.dayOfWeek, "THU", hospitalWorktime.openTime
                                ).as("thuOpenTime"),
                                Expressions.timeTemplate(LocalTime.class,
                                        "MAX(CASE WHEN {0} = {1} THEN {2} END)",
                                        hospitalWorktime.id.dayOfWeek, "THU", hospitalWorktime.closeTime
                                ).as("thuCloseTime"),
                                Expressions.timeTemplate(LocalTime.class,
                                        "MAX(CASE WHEN {0} = {1} THEN {2} END)",
                                        hospitalWorktime.id.dayOfWeek, "THU", hospitalWorktime.breakStartTime
                                ).as("thuBreakStartTime"),
                                Expressions.timeTemplate(LocalTime.class,
                                        "MAX(CASE WHEN {0} = {1} THEN {2} END)",
                                        hospitalWorktime.id.dayOfWeek, "THU", hospitalWorktime.breakEndTime
                                ).as("thuBreakEndTime"),
                                Expressions.timeTemplate(LocalTime.class,
                                        "MAX(CASE WHEN {0} = {1} THEN {2} END)",
                                        hospitalWorktime.id.dayOfWeek, "THU", hospitalWorktime.receptionDeadline
                                ).as("thuReceptionDeadline"),
                                Expressions.booleanTemplate(
                                        "MAX(CASE WHEN {0} = {1} THEN {2} END)",
                                        hospitalWorktime.id.dayOfWeek, "THU", hospitalWorktime.isOpen
                                ).as("thuIsOpen"),

                                // 금요일
                                Expressions.timeTemplate(LocalTime.class,
                                        "MAX(CASE WHEN {0} = {1} THEN {2} END)",
                                        hospitalWorktime.id.dayOfWeek, "FRI", hospitalWorktime.openTime
                                ).as("friOpenTime"),
                                Expressions.timeTemplate(LocalTime.class,
                                        "MAX(CASE WHEN {0} = {1} THEN {2} END)",
                                        hospitalWorktime.id.dayOfWeek, "FRI", hospitalWorktime.closeTime
                                ).as("friCloseTime"),
                                Expressions.timeTemplate(LocalTime.class,
                                        "MAX(CASE WHEN {0} = {1} THEN {2} END)",
                                        hospitalWorktime.id.dayOfWeek, "FRI", hospitalWorktime.breakStartTime
                                ).as("friBreakStartTime"),
                                Expressions.timeTemplate(LocalTime.class,
                                        "MAX(CASE WHEN {0} = {1} THEN {2} END)",
                                        hospitalWorktime.id.dayOfWeek, "FRI", hospitalWorktime.breakEndTime
                                ).as("friBreakEndTime"),
                                Expressions.timeTemplate(LocalTime.class,
                                        "MAX(CASE WHEN {0} = {1} THEN {2} END)",
                                        hospitalWorktime.id.dayOfWeek, "FRI", hospitalWorktime.receptionDeadline
                                ).as("friReceptionDeadline"),
                                Expressions.booleanTemplate(
                                        "MAX(CASE WHEN {0} = {1} THEN {2} END)",
                                        hospitalWorktime.id.dayOfWeek, "FRI", hospitalWorktime.isOpen
                                ).as("friIsOpen"),

                                // 토요일
                                Expressions.timeTemplate(LocalTime.class,
                                        "MAX(CASE WHEN {0} = {1} THEN {2} END)",
                                        hospitalWorktime.id.dayOfWeek, "SAT", hospitalWorktime.openTime
                                ).as("satOpenTime"),
                                Expressions.timeTemplate(LocalTime.class,
                                        "MAX(CASE WHEN {0} = {1} THEN {2} END)",
                                        hospitalWorktime.id.dayOfWeek, "SAT", hospitalWorktime.closeTime
                                ).as("satCloseTime"),
                                Expressions.timeTemplate(LocalTime.class,
                                        "MAX(CASE WHEN {0} = {1} THEN {2} END)",
                                        hospitalWorktime.id.dayOfWeek, "SAT", hospitalWorktime.breakStartTime
                                ).as("satBreakStartTime"),
                                Expressions.timeTemplate(LocalTime.class,
                                        "MAX(CASE WHEN {0} = {1} THEN {2} END)",
                                        hospitalWorktime.id.dayOfWeek, "SAT", hospitalWorktime.breakEndTime
                                ).as("satBreakEndTime"),
                                Expressions.timeTemplate(LocalTime.class,
                                        "MAX(CASE WHEN {0} = {1} THEN {2} END)",
                                        hospitalWorktime.id.dayOfWeek, "SAT", hospitalWorktime.receptionDeadline
                                ).as("satReceptionDeadline"),
                                Expressions.booleanTemplate(
                                        "MAX(CASE WHEN {0} = {1} THEN {2} END)",
                                        hospitalWorktime.id.dayOfWeek, "SAT", hospitalWorktime.isOpen
                                ).as("satIsOpen"),

                                // 일요일
                                Expressions.timeTemplate(LocalTime.class,
                                        "MAX(CASE WHEN {0} = {1} THEN {2} END)",
                                        hospitalWorktime.id.dayOfWeek, "SUN", hospitalWorktime.openTime
                                ).as("sunOpenTime"),
                                Expressions.timeTemplate(LocalTime.class,
                                        "MAX(CASE WHEN {0} = {1} THEN {2} END)",
                                        hospitalWorktime.id.dayOfWeek, "SUN", hospitalWorktime.closeTime
                                ).as("sunCloseTime"),
                                Expressions.timeTemplate(LocalTime.class,
                                        "MAX(CASE WHEN {0} = {1} THEN {2} END)",
                                        hospitalWorktime.id.dayOfWeek, "SUN", hospitalWorktime.breakStartTime
                                ).as("sunBreakStartTime"),
                                Expressions.timeTemplate(LocalTime.class,
                                        "MAX(CASE WHEN {0} = {1} THEN {2} END)",
                                        hospitalWorktime.id.dayOfWeek, "SUN", hospitalWorktime.breakEndTime
                                ).as("sunBreakEndTime"),
                                Expressions.booleanTemplate(
                                        "MAX(CASE WHEN {0} = {1} THEN {2} END)",
                                        hospitalWorktime.id.dayOfWeek, "SUN", hospitalWorktime.isOpen
                                ).as("sunIsOpen")
                        )
                )
                .from(hospital)
                .leftJoin(hospitalWorktime).on(hospital.eq(hospitalWorktime.hospital))
                .leftJoin(treatmentAnimal).on(hospital.eq(treatmentAnimal.hospital))
                .leftJoin(userReview).on(hospital.eq(userReview.hospital))
                .where(likeQ(q), likeRegion(region), withinBounds(bounds), filterByAnimalArray(animalArray), openNowFilter)
                .groupBy(hospital.id);

        NumberExpression<Double> safeAvgRating = userReview.overallRating.avg().coalesce(0.0);

        NumberExpression<Long> safeReviewCount = userReview.id.count().coalesce(0L);


        // 정렬 조건 처리
        if ("distance".equalsIgnoreCase(dto.getSortBy())) {

            if (dto.getCursorDistance() != null && dto.getCursorId() != null) {
                query.where(
                        safeDistance.gt(dto.getCursorDistance())
                                .or(safeDistance.eq(dto.getCursorDistance()).and(hospital.id.gt(dto.getCursorId())))
                );
            }
            query.orderBy(safeDistance.asc(), hospital.id.asc());

        } else if ("rating".equalsIgnoreCase(dto.getSortBy())) {

            if (dto.getCursorRating() != null && dto.getCursorId() != null) {
                query.having(
                        safeAvgRating.lt(dto.getCursorRating())
                                .or(safeAvgRating.eq(dto.getCursorRating()).and(hospital.id.gt(dto.getCursorId())))
                );
            }
            query.orderBy(safeAvgRating.desc(), hospital.id.asc());

        } else if ("reviewCount".equalsIgnoreCase(dto.getSortBy())) {

            if (dto.getCursorReviewCount() != null && dto.getCursorId() != null) {
                query.having(
                        safeReviewCount.lt(dto.getCursorReviewCount())
                                .or(safeReviewCount.eq(dto.getCursorReviewCount()).and(hospital.id.gt(dto.getCursorId())))
                );
            }
            query.orderBy(safeReviewCount.desc(), hospital.id.asc());

        } else {
            if (dto.getCursorId() != null) {
                query.where(hospital.id.gt(dto.getCursorId()));
            }
            query.orderBy(hospital.id.asc());
        }

        List<HospitalSearchDao> result = query.offset(0).limit(dto.getSize()+1).fetch();
        return result;

    }

    // 현재 영업중
    private static BooleanExpression getBooleanExpression(Boolean openNow, String todayStr, LocalTime now) {
        BooleanExpression openNowFilter = null;
        if (Boolean.TRUE.equals(openNow)) {
            // openNow == true 일 때만 현재 영업 중인 병원만 필터링
            openNowFilter = hospitalWorktime.id.dayOfWeek.eq(todayStr)
                    .and(hospitalWorktime.isOpen.isTrue())
                    .and(hospitalWorktime.openTime.loe(now))
                    .and(hospitalWorktime.closeTime.goe(now));
        }
        return openNowFilter;
    }


    // 이름 검색
    private BooleanExpression likeQ(String q) {
        if(StringUtils.hasText(q)){
            return hospital.name.like("%"+q+"%");
        }
        return null;
    }

    // 지역 검색
    private BooleanExpression likeRegion(String region) {
        if(StringUtils.hasText(region)) {
            // 공백 제거 후 LIKE
            return Expressions.stringTemplate(
                    "REPLACE({0}, ' ', '')", hospital.address
            ).like(region + "%");
        }
        return null;
    }

    // 동물 타입 필터링
    private BooleanExpression filterByAnimalArray(String[] animalArray) {
        if (animalArray == null || animalArray.length == 0) return null;

        BooleanExpression in = hospital.id.in(
                JPAExpressions
                        .select(treatmentAnimal.hospital.id)
                        .from(treatmentAnimal)
                        .where(treatmentAnimal.animaType.stringValue().in(animalArray))

        );
        return in;
    }

    // 지도 bounds 필터링 minLat,minLng,maxLat,maxLng
    private BooleanExpression withinBounds(Double[] bounds) {
        if(bounds == null || bounds.length != 4) return null;

        return hospital.lat.between(bounds[0], bounds[2])
                .and(hospital.lng.between(bounds[1], bounds[3]));
    }

    // 거리 계산
    private NumberExpression<Double> calculateDistance(Double lat, Double lng) {
        if (lat == null || lng == null) return null;

        return Expressions.numberTemplate(
                Double.class,
                "ST_Distance_Sphere({0}, ST_GeomFromText({1}, 4326))",
                hospital.location,
                "POINT(" + lat + " " + lng + ")"
        );
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
            speciesCheck =  treat.animaType.eq(AnimalType.valueOf(species));
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
                .where(speciesCheck)
                .groupBy(hospital.id);

        // -----------------------------
        // 필터 적용
        // -----------------------------
        if ("IS_OPEN_NOW".equals(filter)) {
            query.having(isOpenNowFilter.eq(1));   // 현재 영업중인 병원만
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
                                ta -> ta.getAnimaType().getDescription(),
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
                .select(t.animaType)
                .from(t)
                .where(t.hospital.id.eq(hospitalId))
                .fetch()
                .stream()
                .map(AnimalType::getDescription)
                .collect(Collectors.toList());

        // ======================
        // 3) 요일별 worktime 조회
        // ======================
        List<HospitalWorktime> weekly = queryFactory
                .select(w)
                .from(w)
                .where(w.hospital.id.eq(hospitalId))
                .fetch();

        // 오늘 요일
        DayOfWeek today = LocalDate.now().getDayOfWeek();
        LocalTime now = LocalTime.now();

        // 🚨 수정된 부분 1: todayWork 필터링 로직 수정 (NumberFormatException 발생 지점)
        // DB 저장값("FRI")을 Map을 사용하여 정수로 변환하여 비교
        HospitalWorktime todayWork = weekly.stream()
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
                weekly.stream()
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