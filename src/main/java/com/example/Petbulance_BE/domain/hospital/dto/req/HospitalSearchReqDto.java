package com.example.Petbulance_BE.domain.hospital.dto.req;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Arrays;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class HospitalSearchReqDto {

    private String q;

    private String region;

    private Double lat;

    private Double lng;

    private String bounds;

    private String animal;

    private Boolean openNow;

    private String sortBy = "Id";

    private int size = 10;

    private Long cursorId;

    private Double cursorDistance;

    private Double cursorRating;

    private Long cursorReviewCount;

    public Double[] getBounds() {
        if(bounds == null) {
            return new Double[0];
        }
        String[] split = bounds.split(",");

        //bounds=minLat,minLng,maxLat,maxLng
        return new Double[]{Double.valueOf(split[0]), Double.valueOf(split[1]), Double.valueOf(split[2]), Double.valueOf(split[3])};
    }

    public String[] getAnimalArray() {
        if(animal == null) {
            return new String[0];
        }
        String[] arr = animal.split(",");
        return Arrays.stream(arr).map(String::toUpperCase).toArray(String[]::new);
    }

}
