package com.example.Petbulance_BE.domain.dashboard.dto.request;

import com.example.Petbulance_BE.domain.dashboard.type.VisitType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class EventVisitReqDto {
    private VisitType visitType;
}
