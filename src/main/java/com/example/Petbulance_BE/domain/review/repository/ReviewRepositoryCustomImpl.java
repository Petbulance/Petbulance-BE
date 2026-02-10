package com.example.Petbulance_BE.domain.review.repository;

import com.example.Petbulance_BE.domain.review.dto.req.FilterReqDto;
import com.example.Petbulance_BE.domain.review.dto.res.DetailResDto;
import com.example.Petbulance_BE.domain.review.dto.res.FilterResDto;
import com.example.Petbulance_BE.domain.user.entity.Users;
import com.example.Petbulance_BE.global.common.type.AnimalType;
import com.example.Petbulance_BE.global.util.UserUtil;
import com.querydsl.core.Tuple;
import com.querydsl.core.group.GroupBy;
import com.querydsl.core.types.Expression;
import com.querydsl.core.types.ExpressionUtils;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.core.types.dsl.NumberExpression;
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
        Boolean onlyImage = filterReqDto.getImages();
        String sort = filterReqDto.getSort();
        Double cursorRating = filterReqDto.getCursorRating();
        Long cursorLikes = filterReqDto.getCursorLikes();

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
                                ),
                                hospital.userReviews.size().longValue().as("reviewCount")
                            )
                    )
                    .from(userReview)
                    .join(userReview.user, users)
                    .join(userReview.hospital, hospital)
                    .where(checkAnimalType(animalType), checkRegion(region), checkReceiptReview(onlyReceipt), checkHidden(), checkDeleted(), imageValid(onlyImage), cursorCondition(sort, cursorId, cursorRating, cursorLikes))
                    .orderBy(getOrderBy(sort))
                    .limit(filterReqDto.getSize() + 1)
                    .fetch();

    }

    private OrderSpecifier<?>[] getOrderBy(String sort) {
        if ("rating".equals(sort)) { // 별점순
            return new OrderSpecifier[]{userReview.overallRating.desc(), userReview.id.desc()};
        } else if ("likeCount".equals(sort)) { // 좋아요순
            return new OrderSpecifier[]{userReview.likes.size().desc(), userReview.id.desc()};
        }
        // 기본값: 최신순
        return new OrderSpecifier[]{userReview.createdAt.desc(), userReview.id.desc()};
    }

    private BooleanExpression cursorCondition(String sort, Long cursorId, Double cursorRating, Long cursorLikeCount) {

        if (cursorId == null || cursorId == 0) {
            return null;
        }

        // 2. 여기서부터는 '두 번째 페이지' 이후일 때만 실행됨
        if ("rating".equals(sort)) {
            if (cursorRating == null) return null;
            return userReview.overallRating.lt(cursorRating)
                    .or(userReview.overallRating.eq(cursorRating).and(userReview.id.lt(cursorId)));
        }

        if ("likeCount".equals(sort)) {
            if (cursorLikeCount == null) return null;
            NumberExpression<Long> reviewSize = userReview.likes.size().longValue();
            return reviewSize.lt(cursorLikeCount)
                    .or(reviewSize.eq(cursorLikeCount).and(userReview.id.lt(cursorId)));
        }

        // 3. 기본 최신순의 '두 번째 페이지' 조건
        return userReview.id.lt(cursorId);
    }

    private BooleanExpression ltCursorId(Long cursorId) {
        if (cursorId == null || cursorId == 0) {
            return null;
        }

        return userReview.id.lt(cursorId);
    }

    private BooleanExpression imageValid(Boolean onlyImage) {
        if (onlyImage == null || !onlyImage) {
            return null;
        }

        return JPAExpressions
                .selectOne()
                .from(userReviewImage)
                .where(userReviewImage.review.id.eq(userReview.id))
                .exists();
    }

    private BooleanExpression eqId(Long id) {

        return userReview.id.eq(id);
    }


    public BooleanExpression checkAnimalType(List<AnimalType> animalTypes){
        if(animalTypes == null || animalTypes.isEmpty()) return null;

        return userReview.animalType.in(animalTypes);
    }


    public BooleanExpression checkRegion(String region) {
        if (region == null || region.isBlank()) {
            return null;
        }

        String[] regions = region.split(",");

        BooleanExpression expression = null;

        for (String r : regions) {
            String trimmedRegion = r.trim();
            if (trimmedRegion.isEmpty()) continue;

            //각 지역 키워드에 대해 LIKE 조건 생성
            BooleanExpression condition = Expressions.stringTemplate(
                    "REPLACE({0}, ' ', '')", hospital.address
            ).like(trimmedRegion + "%");

            if (expression == null) {
                expression = condition;
            } else {
                expression = expression.or(condition);
            }
        }

        return expression;
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
    public DetailResDto reviewFilterQuery(Long id) {

        Users user = userUtil.getCurrentUser();

        return queryFactory.select(
                        Projections.fields(DetailResDto.class,
                                users.nickname.as("userNickname"),
                                userReview.receiptCheck.as("receiptCheck"),
                                userReview.id.as("id"),
                                userReview.hospital.image.as("hospitalImage"),
                                userReview.hospital.id.as("hospitalId"),
                                userReview.hospital.name.as("hospitalName"),
                                userReview.treatmentService.as("treatmentService"),
                                userReview.visitDate.as("visitDate"),
                                userReview.animalType.as("animalType"),
                                userReview.detailAnimalType.as("detailAnimalType"),
                                userReview.reviewContent.as("reviewContent"),
                                userReview.facilityRating.as("facilityRating"),
                                userReview.expertiseRating.as("expertiseRating"),
                                userReview.kindnessRating.as("kindnessRating"),
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
