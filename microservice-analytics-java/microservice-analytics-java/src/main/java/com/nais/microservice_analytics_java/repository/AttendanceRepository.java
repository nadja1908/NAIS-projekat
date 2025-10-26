package com.nais.microservice_analytics_java.repository;

import com.influxdb.query.FluxTable;
import com.nais.microservice_analytics_java.model.AttendanceRecord;

import java.util.List;

public interface AttendanceRepository {
    void save(AttendanceRecord data);
    List<FluxTable> findAverageUtilizationByRoom(String timeRange);
    List<FluxTable> findLowAttendancePeriods(String timeRange, double utilizationThreshold);
    List<FluxTable> findPeakLoadTimeForCourse(String courseCode, String timeRange);
}
