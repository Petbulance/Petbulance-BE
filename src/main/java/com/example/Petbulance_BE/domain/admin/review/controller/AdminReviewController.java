package com.example.Petbulance_BE.domain.admin.review.controller;

import com.example.Petbulance_BE.domain.admin.page.PageResponse;
import com.example.Petbulance_BE.domain.admin.review.dto.AdminDetailReviewResDto;
import com.example.Petbulance_BE.domain.admin.review.dto.AdminReviewResDto;
import com.example.Petbulance_BE.domain.admin.review.service.AdminReviewService;
import com.example.Petbulance_BE.domain.adminlog.aop.AdminLoggable;
import com.example.Petbulance_BE.domain.adminlog.type.AdminActionType;
import com.example.Petbulance_BE.domain.adminlog.type.AdminPageType;
import com.example.Petbulance_BE.domain.adminlog.type.AdminTargetType;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/admin/review")
public class AdminReviewController {

private final AdminReviewService adminReviewService;

    @GetMapping
    public PageResponse<AdminReviewResDto> getReviewList(@PageableDefault(size = 20, sort = "id", direction = Sort.Direction.DESC)Pageable pageable) {

        return adminReviewService.getReviewListProcess(pageable);

    }

    @GetMapping("/{reviewId}")
    public AdminDetailReviewResDto getDetailReview(@PathVariable Long reviewId, @PageableDefault(size = 20, sort = "reportId", direction = Sort.Direction.DESC) Pageable pageable) {

        return adminReviewService.getDetailReviewProcess(reviewId, pageable);

    }

    @AdminLoggable(
            pageType = AdminPageType.REVIEW_MANAGEMENT,
            actionType = AdminActionType.DELETE,
            targetType = AdminTargetType.REVIEW_STATUS,
            targetId = "#reviewId",
            description = "리뷰 삭제"
    )
    @PatchMapping("/delete/{reviewId}")
    public Map<String, String> deleteUserReview(@PathVariable Long reviewId) {

        return adminReviewService.deleteUserReview(reviewId);

    }

    @AdminLoggable(
            pageType = AdminPageType.REVIEW_MANAGEMENT,
            actionType = AdminActionType.UPDATE,
            targetType = AdminTargetType.REVIEW_STATUS,
            targetId = "#reviewId",
            description = "리뷰 삭제 취소"
    )
    @PatchMapping("/active/{reviewId}")
    public Long activateUserReview(@PathVariable Long reviewId) {

        return adminReviewService.activeUserReviewProcess(reviewId);

    }

}
