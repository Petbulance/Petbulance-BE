package com.example.Petbulance_BE.domain.hospital.dto;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

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
        return animal.split(",");
    }

}
