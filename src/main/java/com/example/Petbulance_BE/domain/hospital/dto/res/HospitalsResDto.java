package com.example.Petbulance_BE.domain.hospital.dto.res;

import com.example.Petbulance_BE.domain.hospital.type.TagType;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class HospitalsResDto {

    private Long hospitalId;

    private String name;

    private Double lat;

    private Double lng;

    private Double distanceMeters;

    private String phone;

    private String[] types;

    private Boolean isOpenNow;

    private String openHours;

    private String thumbnailUrl;

    private Double rating;

    private Long reviewCount;

    private String image;

    @Builder.Default
    private List<Tags> tags = new ArrayList<>();

    @Getter
    @Setter
    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
    public static class Tags{

        private TagType type;

        private String value;

    }

}
