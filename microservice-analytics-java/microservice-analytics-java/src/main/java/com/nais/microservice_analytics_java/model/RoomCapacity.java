package com.nais.microservice_analytics_java.model;

import com.influxdb.annotations.Column;
import com.influxdb.annotations.Measurement;
import lombok.Data;

import java.time.Instant;

@Data
@Measurement(name = "room_capacity")
public class RoomCapacity {
    @Column(tag = true)
    private String roomId;

    @Column(tag = true)
    private String activityType;

    @Column(field = true)
    private Integer maxCapacity;

    @Column(timestamp = true)
    private Instant time;
}
