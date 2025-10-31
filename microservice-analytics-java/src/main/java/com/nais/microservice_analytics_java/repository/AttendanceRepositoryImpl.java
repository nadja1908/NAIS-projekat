package com.nais.microservice_analytics_java.repository;

import com.influxdb.client.InfluxDBClient;
import com.influxdb.client.WriteApi;
import com.influxdb.client.domain.WritePrecision;
import com.influxdb.exceptions.InfluxException;
import com.influxdb.query.FluxTable;
import com.nais.microservice_analytics_java.model.AttendanceRecord;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class AttendanceRepositoryImpl implements AttendanceRepository {

    private final InfluxDBClient influxDBClient;

    @Value("${spring.influx.bucket}")
    private String bucket;
    @Value("${spring.influx.org}")
    private String org;

    public AttendanceRepositoryImpl(InfluxDBClient influxDBClient) {
        this.influxDBClient = influxDBClient;
    }

    @Override
    public void save(AttendanceRecord data) {
        try (WriteApi writeApi = influxDBClient.getWriteApi()) {
            // Koristimo POJO mapping za pisanje
            writeApi.writeMeasurement(bucket, org, WritePrecision.NS, data);
        } catch (InfluxException e) {
            System.err.println("Exception while writing attendance data: " + e.getMessage());
        }
    }

    // 1. Analiza prosečne popunjenosti sale
    @Override
    public List<FluxTable> findAverageUtilizationByRoom(String timeRange) {
        String fluxQuery = String.format("""
            from(bucket: "%s")
              |> range(start: -%s)
              |> filter(fn: (r) => r._measurement == "student_presence")
              |> filter(fn: (r) => r._field == "presentCount") // Filtriramo samo tačke prisustva
              |> group(columns: ["roomId", "activityType", "_time"]) // Grupišemo po sali, tipu aktivnosti I VREMENU
              |> count() // 1. Korak: Broji SVE TAČKE (studente) za TAJ JEDAN TERMIN
              |> rename(columns: {_value: "attendance_count"})
              |> group(columns: ["roomId", "activityType"]) // 2. Korak: Vrati grupu samo po Sali/Aktivnosti
              |> mean(column: "attendance_count") // 3. Korak: Uzmi PROSEK tih prebrojavanja
              |> rename(columns: {_value: "avg_attendance"})
            """, bucket, timeRange
        );
        return influxDBClient.getQueryApi().query(fluxQuery, org);
    }

    // 2. Pronalaženje termina sa niskom posećenošću za potencijalno spajanje
    @Override
    public List<FluxTable> findLowAttendancePeriods(String timeRange, double utilizationThreshold) {
        // Tražimo gde je UKUPAN broj prisutnih manji od praga
        String fluxQuery = String.format("""
                from(bucket: "%s")
                  |> range(start: -%s)
                  |> filter(fn: (r) => r._measurement == "student_presence" and r._field == "present_count")
                  |> aggregateWindow(every: 1h, fn: count, createEmpty: false) // Grupiši po satima
                  |> filter(fn: (r) => r["_value"] < %d) // Filter: manje od praga (simuliramo)
                  |> group(columns: ["roomId", "courseCode", "activityType", "_time"])
                  |> sort(columns: ["_time"], desc: false)
                """, bucket, timeRange, (int)utilizationThreshold
        );
        return influxDBClient.getQueryApi().query(fluxQuery, org);
    }

    @Override
    public List<FluxTable> findPeakLoadTimeForCourse(String courseCode, String timeRange) {
        String fluxQuery = String.format("""
                from(bucket: "%s")
                  |> range(start: -%s)
                  |> filter(fn: (r) => r._measurement == "student_presence")
                  |> filter(fn: (r) => r._field == "present_count")
                  |> filter(fn: (r) => r["courseCode"] == "%s")
                  |> distinct(column: "studentId") // Brojimo jedinstvene
                  |> aggregateWindow(every: 1h, fn: count, createEmpty: false) // Broji prisutne po satu
                  |> group() // Uklanja sve tagove radi pronalaženja MAX vrednosti
                  |> max() // Pronađi maksimalan broj prisutnih
                """, bucket, timeRange, courseCode
        );
        return influxDBClient.getQueryApi().query(fluxQuery, org);
    }
}
