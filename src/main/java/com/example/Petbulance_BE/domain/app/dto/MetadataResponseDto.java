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

    private RegionsResponseDto region = new RegionsResponseDto();

    private List<String> species = new ArrayList<>();

    private List<String> communityCategory = new ArrayList<>();

}
