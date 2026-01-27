package com.example.Petbulance_BE.domain.review.repository;

import com.example.Petbulance_BE.domain.review.dto.req.FilterReqDto;
import com.example.Petbulance_BE.domain.review.dto.res.FilterResDto;
import com.example.Petbulance_BE.domain.user.entity.Users;
import com.example.Petbulance_BE.global.common.type.AnimalType;
import com.example.Petbulance_BE.global.util.UserUtil;
import com.querydsl.core.Tuple;
import com.querydsl.core.group.GroupBy;
import com.querydsl.core.types.Expression;
import com.querydsl.core.types.ExpressionUtils;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.core.types.dsl.NumberPath;
import com.querydsl.jpa.JPAExpressions;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static com.example.Petbulance_BE.domain.hospital.entity.QHospital.hospital;
import static com.example.Petbulance_BE.domain.review.entity.QUserReview.userReview;
import static com.example.Petbulance_BE.domain.review.entity.QUserReviewImage.userReviewImage;
import static com.example.Petbulance_BE.domain.user.entity.QUsers.users;
import static com.example.Petbulance_BE.domain.review.entity.QUserReviewLike.userReviewLike;

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
        Long cursorId = filterReqDto.getCursorId();

        return queryFactory.select(
                    Projections.fields(FilterResDto.class,
                                users.nickname.as("userNickname"),
                                userReview.receiptCheck.as("receiptCheck"),
                                userReview.id.as("id"),
                                userReview.hospital.image.as("hospitalImage"),
                                userReview.hospital.id.as("hospitalId"),
                                userReview.hospital.name.as("hospitalName"),
                                userReview.treatmentService.as("treatmentService"),
                                userReview.animalType.as("animalType"),
                                userReview.detailAnimalType.as("detailAnimalType"),
                                userReview.reviewContent.as("reviewContent"),
                                userReview.overallRating.as("totalRating"),
                                userReview.createdAt.as("createDate"),
                                userReview.totalPrice.as("totalPrice"),
                                ExpressionUtils.as(
                                        JPAExpressions.select(userReviewLike.count())
                                                .from(userReviewLike)
                                                .where(userReviewLike.review.id.eq(userReview.id)),
                                        "likeCount"
                                ),
                                ExpressionUtils.as(
                                        user == null ? Expressions.asBoolean(false) : // 미로그인 시 false
                                                JPAExpressions.select(userReviewLike.id.count().gt(0L)) // 개수가 0보다 크면 true
                                                        .from(userReviewLike)
                                                        .where(userReviewLike.review.id.eq(userReview.id)
                                                                .and(userReviewLike.user.id.eq(user.getId()))),
                                        "liked"
                                )
                            )
                    )
                    .from(userReview)
                    .join(userReview.user, users)
                    .join(userReview.hospital, hospital)
                    .where(checkAnimalType(animalType), checkRegion(region), checkReceiptReview(onlyReceipt), checkHidden(), checkDeleted(), ltCursorId(cursorId))
                    .orderBy(userReview.createdAt.desc(), userReview.id.desc())
                    .limit(filterReqDto.getSize() + 1)
                    .fetch();

    }

    private BooleanExpression ltCursorId(Long cursorId) {
        if (cursorId == null || cursorId == 0) {
            return null;
        }

        return userReview.id.lt(cursorId);
    }

    private BooleanExpression eqId(Long id) {

        return userReview.id.eq(id);
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
    //Q타입 HospitalId를 받는 NumberPath
    private Expression<Long> countTotalReview(NumberPath<Long> hospitalIdPath){

        return Expressions.asNumber(
                JPAExpressions.select(userReview.count())
                        .from(userReview)
                        .where(userReview.hospital.id.eq(hospitalIdPath))

        )
                .longValue()
                .as("totalReviewCount");

    }

    @Override
    public Map<Long, List<String>> findImagesByReviewIds(List<Long> reviewIds) {

        List<Tuple> results = queryFactory
                .select(userReviewImage.review.id, userReviewImage.imageUrl)
                .from(userReviewImage)
                .where(userReviewImage.review.id.in(reviewIds))
                .fetch();

        return results.stream()
                .collect(Collectors.groupingBy(
                        tuple -> tuple.get(userReviewImage.review.id), // Key: 리뷰 ID
                        Collectors.mapping(
                                tuple -> tuple.get(userReviewImage.imageUrl), // Value: 이미지 URL
                                Collectors.toList()
                        )
                ));
    }

    @Override
    public FilterResDto reviewFilterQuery(Long id) {

        Users user = userUtil.getCurrentUser();

        return queryFactory.select(
                        Projections.fields(FilterResDto.class,
                                users.nickname.as("userNickname"),
                                userReview.receiptCheck.as("receiptCheck"),
                                userReview.id.as("id"),
                                userReview.hospital.image.as("hospitalImage"),
                                userReview.hospital.id.as("hospitalId"),
                                userReview.hospital.name.as("hospitalName"),
                                userReview.treatmentService.as("treatmentService"),
                                userReview.animalType.as("animalType"),
                                userReview.detailAnimalType.as("detailAnimalType"),
                                userReview.reviewContent.as("reviewContent"),
                                userReview.overallRating.as("totalRating"),
                                userReview.createdAt.as("createDate"),
                                userReview.totalPrice.as("totalPrice"),
                                ExpressionUtils.as(
                                        JPAExpressions.select(userReviewLike.count())
                                                .from(userReviewLike)
                                                .where(userReviewLike.review.id.eq(userReview.id)),
                                        "likeCount"
                                ),
                                ExpressionUtils.as(
                                        user == null ? Expressions.asBoolean(false) : // 미로그인 시 false
                                                JPAExpressions.select(userReviewLike.id.count().gt(0L)) // 개수가 0보다 크면 true
                                                        .from(userReviewLike)
                                                        .where(userReviewLike.review.id.eq(userReview.id)
                                                                .and(userReviewLike.user.id.eq(user.getId()))),
                                        "liked"
                                )
                        )
                )
                .from(userReview)
                .join(userReview.user, users)
                .join(userReview.hospital, hospital)
                .where(checkHidden(), checkDeleted(), eqId(id))
                .limit(1)
                .fetchOne();

    }

}
