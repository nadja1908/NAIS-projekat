package com.nais.microservice_analytics_java.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RoomUtilizationDTO {
    private String roomId;
    private String activityType;
    private Double averageAttendance;
    private Integer roomCapacity;
    private Double utilizationRate;
    private String optimizationSuggestion;
}
