package com.example.Petbulance_BE.domain.dashboard.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class DashBoardNoticeStatusResDto {
    private Long activeAdBanner;
    private Long registeredHospital;
}
