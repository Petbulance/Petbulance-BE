package com.example.Petbulance_BE.domain.review.dto.res;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
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

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private Double nextCursorRating;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private Integer nextCursorLikeCount;

    private Boolean hasNext;
}
