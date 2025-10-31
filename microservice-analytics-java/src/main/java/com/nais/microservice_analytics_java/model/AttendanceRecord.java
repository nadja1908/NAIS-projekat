package com.nais.microservice_analytics_java.model;

import com.influxdb.annotations.Column;
import com.influxdb.annotations.Measurement;
import lombok.Data;

import java.time.Instant;

@Data
@Measurement(name = "student_presence")
public class AttendanceRecord {
    @Column(tag = true)
    private String studentId;

    @Column(tag = true)
    private String courseCode;

    @Column(tag = true)
    private String activityType;

    @Column(tag = true)
    private String roomId;

    @Column
    private Long presentCount = 1L;

    @Column(timestamp = true)
    private Instant time;
}
