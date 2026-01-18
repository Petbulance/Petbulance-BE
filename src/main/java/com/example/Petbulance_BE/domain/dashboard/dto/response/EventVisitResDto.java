package com.example.Petbulance_BE.domain.dashboard.dto.response;

import com.example.Petbulance_BE.domain.dashboard.type.VisitType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class EventVisitResDto {
    private String message;
    private VisitType visitType;
}
