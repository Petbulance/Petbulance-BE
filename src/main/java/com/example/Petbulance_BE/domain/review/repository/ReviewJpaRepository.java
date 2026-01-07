package com.example.Petbulance_BE.domain.review.repository;

import com.example.Petbulance_BE.domain.admin.review.dto.AdminReviewResDto;
import com.example.Petbulance_BE.domain.hospital.dto.UserReviewSearchDto;
import com.example.Petbulance_BE.domain.review.dto.MyReviewGetDto;
import com.example.Petbulance_BE.domain.review.entity.UserReview;
import com.example.Petbulance_BE.domain.user.entity.Users;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ReviewJpaRepository extends JpaRepository<UserReview, Long>, ReviewRepositoryCustom {
    //-- 커서 조건: 이전 페이지의 마지막 ID보다 작은 리뷰를 가져옴
    //-- 정렬 기준: ID를 내림차순 (최신순 또는 ID가 큰 순서대로)
    @Query(value = """
    SELECT new com.example.Petbulance_BE.domain.hospital.dto.UserReviewSearchDto(
    r.receiptCheck, r.id, h.image, r.hospital.id, h.name, r.treatmentService, r.detailAnimalType, r.reviewContent, r.overallRating
    )
    FROM UserReview r
    JOIN Hospital h ON h.id = r.hospital.id
    WHERE ( r.treatmentService LIKE CONCAT('%', :search, '%') OR h.name LIKE CONCAT('%', :search, '%') )
    AND (:cursorId IS NULL OR r.id < :cursorId)
    AND (r.hidden = FALSE )
    AND (r.deleted = FALSE )
    ORDER BY r.id DESC
    """)
    List<UserReviewSearchDto> findByHospitalNameOrTreatmentService(
            @Param("search") String search,
            @Param("cursorId") Long cursorId, // 새로 추가된 커서 ID
            Pageable pageable // 페이징 정보를 받기 위한 Pageable 객체
    );

    @EntityGraph(attributePaths = {"user"})
    Page<UserReview> findByHospitalId(Long hospitalId, Pageable pageable);

    @Query("""
    SELECT ur 
    FROM UserReview ur
    JOIN FETCH ur.user u
    WHERE ur.hospital.id = :hospitalId
    AND EXISTS (
        SELECT 1 FROM UserReviewImage i WHERE i.review = ur
    )
    AND (ur.hidden = FALSE )
    AND (ur.deleted = FALSE )
    """)
    Page<UserReview> findByHospitalIdWithImages(@Param("hospitalId") Long hospitalId, Pageable pageable);

    Optional<UserReview> findByIdAndUserId(Long id, String userId);

    //최신순
    @Query(value = """
    SELECT r
    FROM UserReview r
    JOIN FETCH r.user u
    WHERE r.hospital.id = :hospitalId
    AND (:imageOnly = FALSE OR EXISTS (SELECT 1 FROM UserReviewImage i WHERE i.review = r))
    AND (:cursorId IS NULL OR r.id < :cursorId)
    AND (r.hidden = FALSE )
    AND (r.deleted = FALSE )
    ORDER BY r.id DESC
    """)
    List<UserReview> findByHospitalIdOrderByLatest(
            @Param("hospitalId") Long hospitalId,
            @Param("imageOnly") Boolean imageOnly,
            @Param("cursorId") Long cursorId,
            Pageable pageable
    );

    //별점순
    @Query(value = """
    SELECT r
    FROM UserReview r
    JOIN FETCH r.user u
    WHERE r.hospital.id = :hospitalId
    AND (:imageOnly = FALSE OR EXISTS (SELECT 1 FROM UserReviewImage i WHERE i.review = r))
    AND (:cursorRating IS NULL OR
         r.overallRating < :cursorRating OR
         (r.overallRating = :cursorRating AND r.id < :cursorId)
        )
    AND (r.hidden = FALSE )
    AND (r.deleted = FALSE )
    ORDER BY r.overallRating DESC, r.id DESC
    """)
    List<UserReview> findByHospitalIdOrderByRating(
            @Param("hospitalId") Long hospitalId,
            @Param("imageOnly") Boolean imageOnly,
            @Param("cursorRating") Double cursorRating,
            @Param("cursorId") Long cursorId,
            Pageable pageable
    );

    //좋아요순
    @Query(value = """
    SELECT r
    FROM UserReview r
    JOIN FETCH r.user u
    WHERE r.hospital.id = :hospitalId
    AND (:imageOnly = FALSE OR EXISTS (SELECT 1 FROM UserReviewImage i WHERE i.review = r))
    AND (:cursorLikeCount IS NULL OR
         (SELECT COUNT(li) FROM UserReviewLike li WHERE li.review = r) < :cursorLikeCount OR
         ((SELECT COUNT(li) FROM UserReviewLike li WHERE li.review = r) = :cursorLikeCount AND r.id < :cursorId)
        )
    AND (r.hidden = FALSE )
    AND (r.deleted = FALSE )
    ORDER BY (SELECT COUNT(li) FROM UserReviewLike li WHERE li.review = r) DESC, r.id DESC
    """)
        List<UserReview> findByHospitalIdOrderByLikeCount(
            @Param("hospitalId") Long hospitalId,
            @Param("imageOnly") Boolean imageOnly,
            @Param("cursorLikeCount") Long cursorLikeCount,
            @Param("cursorId") Long cursorId,
            Pageable pageable
    );

    @Query("SELECT u FROM UserReview u WHERE u.user = :user AND u.id = :reviewId")
    Optional<UserReview> findByUserId(@Param("user") Users user, @Param("reviewId") Long reviewId);

    @Query(value = """
            SELECT new com.example.Petbulance_BE.domain.review.dto.MyReviewGetDto(
            r.id,
            r.hospital.name,
            r.hospital.image,
            r.createdAt,
            r.receiptCheck,
            SIZE(r.likes),
            r.reviewContent
            )
            FROM UserReview r
            WHERE r.user = :user
            AND (r.deleted = FALSE )
            AND (:cursorId IS NULL OR r.id < :cursorId)
            order by r.id DESC 
            """)
    List<MyReviewGetDto> findByUserIdAndCursorId(@Param("user") Users user, @Param("cursorId") Long cursorId, Pageable pageable);

    @Query("SELECT new com.example.Petbulance_BE.domain.admin.review.dto.AdminReviewResDto(" +
            "ur.id, " +
            "h.name, " +
            "u.nickname, " +
            "ur.createdAt, " +
            "CASE " +
            "  WHEN ur.deleted = true THEN 'deleted' " +
            "  WHEN ur.hidden = true THEN 'hidden' " +
            "  ELSE 'normal' " +
            "END, " +
            "SIZE(ur.reviewReports)) " +
            "FROM UserReview ur " +
            "LEFT JOIN ur.hospital h " +
            "LEFT JOIN ur.user u")
    Page<AdminReviewResDto> adminGetReviewList(Pageable pageable);

}
