package com.nais.microservice_analytics_java.service;

import com.influxdb.query.FluxRecord;
import com.influxdb.query.FluxTable;
import com.nais.microservice_analytics_java.dto.LowAttendanceDTO;
import com.nais.microservice_analytics_java.dto.PeakLoadDTO;
import com.nais.microservice_analytics_java.dto.RoomUtilizationDTO;
import com.nais.microservice_analytics_java.model.AttendanceRecord;
import com.nais.microservice_analytics_java.repository.AttendanceRepository;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
public class AttendanceService {

    private final AttendanceRepository attendanceRepository;
    private static final Map<String, Integer> ROOM_CAPACITIES = Map.of(
            "B205", 100,
            "A101", 30,
            "LAB_3", 25,
            "HALL_1", 250
    );

    public AttendanceService(AttendanceRepository attendanceRepository) {
        this.attendanceRepository = attendanceRepository;
    }

    public void recordPresence(AttendanceRecord data) {
        if (data.getTime() == null) {
            data.setTime(Instant.now());
        }
        attendanceRepository.save(data);
    }

    // 1. Uslužna funkcija za parsiranje i dodavanje logike optimizacije
    public List<RoomUtilizationDTO> getRoomUtilizationAnalysis(String timeRange) {
        List<FluxTable> fluxTables = attendanceRepository.findAverageUtilizationByRoom(timeRange);
        List<RoomUtilizationDTO> results = new ArrayList<>();

        for (FluxTable table : fluxTables) {
            for (FluxRecord record : table.getRecords()) {
                String roomId = (String) record.getValueByKey("roomId");
                String activityType = (String) record.getValueByKey("activityType");
                Double avgAttendance = (Double) record.getValueByKey("avg_attendance");
                Integer capacity = ROOM_CAPACITIES.getOrDefault(roomId, 50); // Uzmi kapacitet ili podrazumevanu vrednost

                double utilizationRate = (avgAttendance != null && capacity > 0) ? (avgAttendance / capacity) : 0.0;
                String suggestion = getUtilizationSuggestion(utilizationRate);

                results.add(new RoomUtilizationDTO(roomId, activityType, avgAttendance, capacity, utilizationRate, suggestion));
            }
        }
        return results;
    }

    // 2. Analiza niskog prisustva
    public List<LowAttendanceDTO> getLowAttendancePeriods(String timeRange, double threshold) {
        List<FluxTable> fluxTables = attendanceRepository.findLowAttendancePeriods(timeRange, threshold);
        List<LowAttendanceDTO> results = new ArrayList<>();

        for (FluxTable table : fluxTables) {
            for (FluxRecord record : table.getRecords()) {
                Instant time = record.getTime();
                String roomId = (String) record.getValueByKey("roomId");
                String courseCode = (String) record.getValueByKey("courseCode");
                String activityType = (String) record.getValueByKey("activityType");
                // _value ovde predstavlja count po agregiranom prozoru (satu)
                Long recordedAttendance = ((Number) record.getValueByKey("_value")).longValue();

                String suggestion = String.format("Prisustvo ispod %.0f: Predložiti spajanje ovog termina sa drugim ili prebacivanje u manju salu.", threshold);

                results.add(new LowAttendanceDTO(time, roomId, courseCode, activityType, recordedAttendance, suggestion));
            }
        }
        return results;
    }

    // 3. Analiza maksimalnog opterećenja
    public List<PeakLoadDTO> getPeakLoadAnalysis(String courseCode, String timeRange) {
        List<FluxTable> fluxTables = attendanceRepository.findPeakLoadTimeForCourse(courseCode, timeRange);
        List<PeakLoadDTO> results = new ArrayList<>();

        for (FluxTable table : fluxTables) {
            for (FluxRecord record : table.getRecords()) {
                Long peakCount = ((Number) record.getValueByKey("_value")).longValue();

                String suggestion = getPeakLoadSuggestion(peakCount, courseCode);
                results.add(new PeakLoadDTO(courseCode, peakCount, suggestion));
            }
        }
        return results;
    }

    private String getUtilizationSuggestion(double rate) {
        if (rate > 0.85) {
            return "🔥 Visoka popunjenost: Razmotriti deobu u manje grupe ili prebacivanje u veću salu, ako je raspoloživa.";
        } else if (rate < 0.3) {
            return "🧊 Niska popunjenost: Preporučena **optimizacija** - prebacivanje u manju salu radi uštede resursa.";
        } else {
            return "✅ Optimalna popunjenost: Sala adekvatno iskorišćena.";
        }
    }

    private String getPeakLoadSuggestion(Long peakCount, String courseCode) {
        Integer capacity = ROOM_CAPACITIES.values().stream().mapToInt(i -> i).max().orElse(300); // Maksimalni kapacitet fakulteta
        if (peakCount > capacity) {
            return "‼️ Kritično preopterećenje: Maksimalno prisustvo prelazi ukupan najveći kapacitet sale, neophodna deoba kursa u više grupa.";
        } else if (peakCount > 150) {
            return "⚠️ Visoko opterećenje: Kurs zahteva veliku salu/amfiteatar. Razmotriti dvodelna predavanja.";
        } else {
            return "🟢 Normalno opterećenje: Standardna sala dovoljna.";
        }
    }
}
