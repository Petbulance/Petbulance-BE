package com.example.Petbulance_BE.domain.review.repository;

import com.example.Petbulance_BE.domain.review.dto.req.FilterReqDto;
import com.example.Petbulance_BE.domain.review.dto.res.FilterResDto;
import com.example.Petbulance_BE.domain.user.entity.Users;
import com.example.Petbulance_BE.global.common.type.AnimalType;
import com.example.Petbulance_BE.global.util.UserUtil;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;

import static com.example.Petbulance_BE.domain.hospital.entity.QHospital.hospital;
import static com.example.Petbulance_BE.domain.review.entity.QUserReview.userReview;

@Repository
@RequiredArgsConstructor
public class ReviewRepositoryCustomImpl implements ReviewRepositoryCustom {

    private final JPAQueryFactory queryFactory;
    private final UserUtil userUtil;

    @Override
    public List<FilterResDto> reviewFilterQuery(FilterReqDto filterReqDto) {

        Users user = userUtil.getCurrentUser();

        List<AnimalType> animalType = filterReqDto.getAnimalType();
        String region = filterReqDto.getRegion();
        Boolean onlyReceipt = filterReqDto.getReceipt();

        return queryFactory.select(
                    Projections.fields(FilterResDto.class,
                                userReview.receiptCheck.as("receiptCheck"),
                                userReview.id.as("id"),
                                userReview.hospital.image.as("hospitalImage"),
                                userReview.hospital.id.as("hospitalId"),
                                userReview.hospital.name.as("hospitalName"),
                                userReview.treatmentService.as("treatmentService"),
                                userReview.detailAnimalType.as("detailAnimalType"),
                                userReview.reviewContent.as("reviewContent"),
                                userReview.overallRating.as("totalRating")
                            )
            )
                    .from(userReview)
                    .join(hospital).on(hospital.eq(userReview.hospital))
                    .where(checkAnimalType(animalType), checkRegion(region), checkReceiptReview(onlyReceipt), checkHidden(), checkDeleted())
                    .orderBy(userReview.createdAt.desc())
                    .fetch();

    }

    public BooleanExpression checkAnimalType(List<AnimalType> animalTypes){
        if(animalTypes == null || animalTypes.isEmpty()) return null;

        return userReview.animalType.in(animalTypes);
    }


    public BooleanExpression checkRegion(String region){
        if(region == null) return null;

        return Expressions.stringTemplate(
                "REPLACE({0}, ' ', '')", hospital.address
        ).like(region + "%");
    }

    public BooleanExpression checkReceiptReview(Boolean receiptCheck){
        if(receiptCheck == null || receiptCheck == false) return null;

        return userReview.receiptCheck.eq(receiptCheck);

    }

    public BooleanExpression checkHidden(){

        return userReview.hidden.eq(Boolean.FALSE);

    }

    public BooleanExpression checkDeleted(){

        return userReview.deleted.eq(Boolean.FALSE);

    }

    public BooleanExpression checkUser(Users user){

        return userReview.user.eq(user);

    }

}
