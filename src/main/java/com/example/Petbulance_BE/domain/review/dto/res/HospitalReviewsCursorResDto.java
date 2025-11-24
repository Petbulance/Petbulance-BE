package com.example.Petbulance_BE.domain.review.dto.res;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class HospitalReviewsCursorResDto {

    private List<SearchResDto> list;

    private Long nextCursorId;
    @JsonIgnore
    private Double nextCursorRating;
    @JsonIgnore
    private Integer nextCursorLikeCount;

    private Boolean hasNext;
}
