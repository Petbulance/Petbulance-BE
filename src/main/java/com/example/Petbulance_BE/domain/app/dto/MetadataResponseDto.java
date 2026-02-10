package com.example.Petbulance_BE.domain.app.dto;

import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class MetadataResponseDto {

    @Builder.Default
    private RegionsResponseDto region = new RegionsResponseDto();

    @Builder.Default
    private List<String> species = new ArrayList<>();

    @Builder.Default
    private List<String> communityCategory = new ArrayList<>();

}
