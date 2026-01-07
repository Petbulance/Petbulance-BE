package com.example.Petbulance_BE.domain.admin.user.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class ReviewBanReqDto {

    private String id;

    private int day;

    private String reason;

}
