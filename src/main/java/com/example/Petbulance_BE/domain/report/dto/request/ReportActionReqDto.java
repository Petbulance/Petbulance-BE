package com.example.Petbulance_BE.domain.report.dto.request;

import com.example.Petbulance_BE.domain.report.type.ReportActionType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ReportActionReqDto {
    private ReportActionType actionType;
}
