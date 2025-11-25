package com.example.Petbulance_BE.domain.review.dto.req;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ReviewImageCheckReqDto {

    private Long reviewId;

    private List<String> keys = new ArrayList<>();

}
