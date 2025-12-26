package com.example.Petbulance_BE.domain.dashboard.dto;

import com.example.Petbulance_BE.domain.dashboard.type.ChangeType;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class MetricResult {
    private int todayCount;
    private double changeRate;
    private ChangeType trend;
}

