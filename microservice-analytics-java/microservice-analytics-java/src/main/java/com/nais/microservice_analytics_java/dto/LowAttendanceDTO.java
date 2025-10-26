package com.nais.microservice_analytics_java.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class LowAttendanceDTO {
    private Instant time;
    private String roomId;
    private String courseCode;
    private String activityType;
    private Long recordedAttendance;
    private String optimizationSuggestion;
}
