package com.example.Petbulance_BE.domain.app.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class RegionsResponseDto {

    private List<String> regions1List = new ArrayList<>();

    private List<String> regions2List = new ArrayList<>();

}
