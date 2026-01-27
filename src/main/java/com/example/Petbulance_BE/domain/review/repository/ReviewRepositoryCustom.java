package com.example.Petbulance_BE.domain.review.repository;

import com.example.Petbulance_BE.domain.review.dto.req.FilterReqDto;
import com.example.Petbulance_BE.domain.review.dto.res.FilterResDto;
import com.example.Petbulance_BE.domain.review.dto.res.HospitalUserReviewResDto;

import java.util.List;
import java.util.Map;

public interface ReviewRepositoryCustom {

    List<FilterResDto> reviewFilterQuery(FilterReqDto filterReqDto);

    Map<Long, List<String>> findImagesByReviewIds(List<Long> reviewIds);

    FilterResDto reviewFilterQuery(Long id);
}
