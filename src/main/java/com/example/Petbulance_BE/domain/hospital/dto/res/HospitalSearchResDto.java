package com.example.Petbulance_BE.domain.hospital.dto.res;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;

import java.util.List;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class HospitalSearchResDto {

    private List<HospitalsResDto> list;

    private Long cursorId;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private Double cursorDistance;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private Double cursorRating;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private Long cursorReviewCount;

    private boolean hasNext;

}
