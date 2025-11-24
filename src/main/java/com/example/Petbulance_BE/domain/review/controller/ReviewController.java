package com.example.Petbulance_BE.domain.review.controller;

import com.example.Petbulance_BE.domain.hospital.dto.UserReviewSearchDto;
import com.example.Petbulance_BE.domain.review.dto.CursorPagingResDto;
import com.example.Petbulance_BE.domain.review.dto.res.HospitalReviewsCursorResDto;
import com.example.Petbulance_BE.domain.review.dto.req.FilterReqDto;
import com.example.Petbulance_BE.domain.review.dto.req.ReviewImageCheckReqDto;
import com.example.Petbulance_BE.domain.review.dto.req.ReviewSaveReqDto;
import com.example.Petbulance_BE.domain.review.dto.res.*;
import com.example.Petbulance_BE.domain.review.service.ReviewService;
import com.example.Petbulance_BE.global.common.error.exception.CustomException;
import com.example.Petbulance_BE.global.common.error.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import reactor.core.publisher.Mono;

import java.util.Map;


@RestController
@RequestMapping("/receipts")
@RequiredArgsConstructor
public class ReviewController {

    private final ReviewService reviewService;

    //영수증 리뷰 영수증 인식 기능
    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public Mono<ReceiptResDto> receiptExtract(@RequestParam("image") MultipartFile image) {

        if(image.isEmpty()){
            throw new CustomException(ErrorCode.NOT_FOUND_RECEIPT);
        }

        return reviewService.receiptExtractProcess(image);
    }
    //영수증 리뷰 작성 중 병원명 변경 조회
    @GetMapping("/{hospitalName}")
    public FindHospitalResDto findHospital(@PathVariable String hospitalName) {

        return reviewService.findHospitalProcess(hospitalName);

    }
    //병원 후기 병원명, 진료명 검색 조회
    @GetMapping("/search/{value}")
    public CursorPagingResDto<UserReviewSearchDto> searchReview(@PathVariable String value,
                                                                @RequestParam(value = "cursorId", required = false) Long cursorId,
                                                                @RequestParam(value = "size", defaultValue = "10") int size) {

        return reviewService.searchReviewProcess(value, cursorId, size);

    }
    //병원 후기 필터 조회
    @GetMapping("/filter")
    public CursorPagingResDto<FilterResDto> filterReview(@ModelAttribute FilterReqDto filterReqDto) {

        return reviewService.filterReviewProcess(filterReqDto);

    }
    //병원 상세 페이지 조회, 이미지 추가 필요
    @GetMapping("/reviews/{hospitalId}")
    public HospitalReviewsCursorResDto hospitalReviews(
            @PathVariable("hospitalId") Long hospitalId,
            @RequestParam(value = "images", defaultValue = "false") Boolean onlyImageReview,

            @RequestParam(value = "cursorId", required = false) Long cursorId,
            @RequestParam(value = "cursorRating", required = false) Double cursorRating,
            @RequestParam(value = "cursorLikeCount", required = false) Long cursorLikeCount,

            @RequestParam(value = "size", defaultValue = "10") int size,
            @RequestParam(value = "sortBy", defaultValue = "createdAt") String sortBy,
            @RequestParam(value = "sortDirection", defaultValue = "desc") String sortDirection
    ) {

        return reviewService.hospitalReviewsProcess(hospitalId, onlyImageReview, cursorId, cursorRating, cursorLikeCount, size, sortBy, sortDirection);

    }

    @PostMapping("/save/reviews")
    public ReviewSaveResDto saveHospitalReview(@RequestBody ReviewSaveReqDto reviewSaveReqDto){

            return reviewService.saveHospitalReviewProcess(reviewSaveReqDto);

    }

    @GetMapping("/save/success")
    public Map<String, String> reviewImageSaveCheck(@RequestBody ReviewImageCheckReqDto reviewImageCheckReqDto){

        reviewService.reviewImageSaveCheckProcess(reviewImageCheckReqDto);

        return Map.of("message", "이미지 저장에 성공하였습니다.");

    }

}
