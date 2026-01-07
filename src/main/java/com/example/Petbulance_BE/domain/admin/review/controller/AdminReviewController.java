package com.example.Petbulance_BE.domain.admin.review.controller;

import com.example.Petbulance_BE.domain.admin.page.PageResponse;
import com.example.Petbulance_BE.domain.admin.review.dto.AdminDetailReviewResDto;
import com.example.Petbulance_BE.domain.admin.review.dto.AdminReviewResDto;
import com.example.Petbulance_BE.domain.admin.review.service.AdminReviewService;
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
    public AdminDetailReviewResDto getDetailReview(@PathVariable Long reviewId) {

        return adminReviewService.getDetailReviewProcess(reviewId);

    }

    @PatchMapping("/delete/{reviewId}")
    public Map<String, String> deleteUserReview(@PathVariable Long reviewId) {

        return adminReviewService.deleteUserReview(reviewId);

    }

    @PatchMapping("/active/{reviewId}")
    public Long activateUserReview(@PathVariable Long reviewId) {

        return adminReviewService.activeUserReviewProcess(reviewId);

    }

}
