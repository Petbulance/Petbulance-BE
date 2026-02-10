package com.example.Petbulance_BE.domain.admin.version.dto;

import lombok.*;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class RegionResDto {

    List<Region1Dto> region1;

    List<Region2Dto> region2;

    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    @Getter
    public static class Region1Dto {

        private Long id;

        private String name;

        private String code;

    }

    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    @Getter
    public static class Region2Dto {

        private Long id;

        private String name;

        private String code;

        private Long superiorId;

    }


}
