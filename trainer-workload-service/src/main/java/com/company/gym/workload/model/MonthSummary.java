package com.company.gym.workload.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MonthSummary {
    private int monthValue; // 1-12
    private long totalDuration;
}