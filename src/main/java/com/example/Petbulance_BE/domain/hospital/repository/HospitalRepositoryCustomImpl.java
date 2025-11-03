package com.example.Petbulance_BE.domain.hospital.repository;

import com.example.Petbulance_BE.domain.hospital.dto.HospitalSearchReqDto;
import com.example.Petbulance_BE.domain.hospital.dto.HospitalSearchRes;
import com.example.Petbulance_BE.domain.hospital.entity.QHospital;
import com.example.Petbulance_BE.domain.hospitalWorktime.entity.QHospitalWorktime;
import com.example.Petbulance_BE.domain.review.entity.QUserReview;
import com.example.Petbulance_BE.domain.treatmentAnimal.entity.QTreatmentAnimal;
import com.querydsl.core.types.Expression;
import com.querydsl.core.types.ExpressionUtils;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.*;
import com.querydsl.jpa.JPAExpressions;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.PageImpl;

import org.springframework.stereotype.Repository;
import org.springframework.util.StringUtils;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

import static com.example.Petbulance_BE.domain.hospital.entity.QHospital.hospital;
import static com.example.Petbulance_BE.domain.hospitalWorktime.entity.QHospitalWorktime.hospitalWorktime;
import static com.example.Petbulance_BE.domain.review.entity.QUserReview.userReview;
import static com.example.Petbulance_BE.domain.treatmentAnimal.entity.QTreatmentAnimal.treatmentAnimal;
import static com.querydsl.core.types.dsl.Expressions.numberTemplate;

@Repository
@RequiredArgsConstructor
public class HospitalRepositoryCustomImpl implements HospitalRepositoryCustom {

    private final JPAQueryFactory queryFactory;

    @Override
    public Page<HospitalSearchRes> searchHospitals(HospitalSearchReqDto dto, Pageable pageable) {
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

        NumberExpression<Double> doubleNumberExpression = calculateDistance(lng, lat);

        JPAQuery<HospitalSearchRes> query = queryFactory.select(
                        Projections.fields(HospitalSearchRes.class,
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
                .groupBy(hospital.id)
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize());

        if (doubleNumberExpression != null) {
            query = query.orderBy(doubleNumberExpression.asc(), hospital.id.asc());
        } else {
            query = query.orderBy(hospital.id.asc());
        }

        List<HospitalSearchRes> result = query.fetch();

        Long total = queryFactory
                .select(hospital.id.countDistinct())
                .from(hospital)
                .leftJoin(hospitalWorktime).on(hospital.eq(hospitalWorktime.hospital))
                .leftJoin(treatmentAnimal).on(hospital.eq(treatmentAnimal.hospital))
                .where(likeQ(q), likeRegion(region), withinBounds(bounds),
                        filterByAnimalArray(animalArray), openNowFilter)
                .fetchOne();

        if(total == null) total = 0L;

        return new PageImpl<>(result, pageable, total);
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
                "POINT(" + lng + " " + lat + ")"
        );
    }


}
