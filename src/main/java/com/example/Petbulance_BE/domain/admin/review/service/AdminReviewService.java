package com.example.Petbulance_BE.domain.admin.review.service;

import com.example.Petbulance_BE.domain.admin.page.PageResponse;
import com.example.Petbulance_BE.domain.admin.review.dto.AdminDetailReviewResDto;
import com.example.Petbulance_BE.domain.admin.review.dto.AdminReviewResDto;
import com.example.Petbulance_BE.domain.report.entity.Report;
import com.example.Petbulance_BE.domain.report.repository.ReportRepository;
import com.example.Petbulance_BE.domain.report.type.ReportType;
import com.example.Petbulance_BE.domain.review.entity.UserReview;
import com.example.Petbulance_BE.domain.review.entity.UserReviewImage;
import com.example.Petbulance_BE.domain.review.repository.ReviewJpaRepository;
import com.example.Petbulance_BE.global.common.error.exception.CustomException;
import com.example.Petbulance_BE.global.common.error.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class AdminReviewService {

    private final ReviewJpaRepository reviewJpaRepository;
    private final ReportRepository reportRepository;

    public PageResponse<AdminReviewResDto> getReviewListProcess(Pageable pageable) {

        Page<AdminReviewResDto> list = reviewJpaRepository.adminGetReviewList(pageable);

        return new PageResponse<AdminReviewResDto>(list);

    }

    @Transactional
    public AdminDetailReviewResDto getDetailReviewProcess(Long reviewId, Pageable pageable) {

        UserReview userReview = reviewJpaRepository.findById(reviewId).orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND_REVIEW));
        Page<Report> report = reportRepository.findAllByReportType(ReportType.REVIEW, pageable);

        Page<AdminDetailReviewResDto.ReviewHistoryResDto> historyDtoPage = report.map(AdminDetailReviewResDto.ReviewHistoryResDto::new);
        PageResponse<AdminDetailReviewResDto.ReviewHistoryResDto> historyResponse = new PageResponse<>(historyDtoPage);

        return AdminDetailReviewResDto.builder()
                    .hospitalName(userReview.getHospital().getName())
                    .address(userReview.getHospital().getAddress())
                    .roadAddress(userReview.getHospital().getStreetAddress())
                    .latitude(userReview.getHospital().getLat())
                    .longitude(userReview.getHospital().getLng())
                    .treatmentService(userReview.getTreatmentService())
                    .images(userReview.getImages().stream().map(UserReviewImage::getImageUrl).toList())
                    .title(userReview.getTitle())
                    .totalPrice(userReview.getTotalPrice())
                    .facilityRating(userReview.getFacilityRating())
                    .expertiseRating(userReview.getExpertiseRating())
                    .kindnessRating(userReview.getKindnessRating())
                    .reviewContent(userReview.getReviewContent())
                    .history(historyResponse)
                    .build();

    }

    @Transactional
    public Map<String, String> deleteUserReview(Long reviewId) {

        UserReview userReview = reviewJpaRepository.findById(reviewId).orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND_REVIEW));

        userReview.setDeleted(true);

        return Map.of("message", "success");
    }

    @Transactional
    public Long activeUserReviewProcess(Long reviewId) {

        UserReview userReview = reviewJpaRepository.findById(reviewId).orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND_REVIEW));

        userReview.setDeleted(false);

        return reviewJpaRepository.save(userReview).getId();
    }
}
