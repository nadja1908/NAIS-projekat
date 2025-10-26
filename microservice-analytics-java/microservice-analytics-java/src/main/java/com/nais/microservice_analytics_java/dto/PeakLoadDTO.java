package com.nais.microservice_analytics_java.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PeakLoadDTO {
    private String courseCode;
    private Long peakAttendanceCount;
    private String optimizationSuggestion;
}
