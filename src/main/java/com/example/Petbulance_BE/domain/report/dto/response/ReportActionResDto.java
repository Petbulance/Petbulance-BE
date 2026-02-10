package com.example.Petbulance_BE.domain.report.dto.response;

import com.example.Petbulance_BE.domain.report.type.ReportActionType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ReportActionResDto {
    private ReportActionType reportActionType;
    private String message;
}
