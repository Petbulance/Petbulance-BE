package com.example.Petbulance_BE.domain.app.dto;

import lombok.*;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MetadataRequestDto {

    private String region;

    private String species;

    private String communityCategory;

}
