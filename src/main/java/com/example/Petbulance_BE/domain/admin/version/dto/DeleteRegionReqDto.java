package com.example.Petbulance_BE.domain.admin.version.dto;

import com.example.Petbulance_BE.domain.admin.version.type.RegionType;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class DeleteRegionReqDto {

    private RegionType type;

    private Long id;

    private String version;

}
