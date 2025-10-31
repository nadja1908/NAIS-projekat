package com.nais.microservice_analytics_java.controller;

import com.nais.microservice_analytics_java.dto.LowAttendanceDTO;
import com.nais.microservice_analytics_java.dto.PeakLoadDTO;
import com.nais.microservice_analytics_java.dto.RoomUtilizationDTO;
import com.nais.microservice_analytics_java.model.AttendanceRecord;
import com.nais.microservice_analytics_java.service.AttendanceService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

// AttendanceController.java
@RestController
@RequestMapping("/api/v1/attendance")
public class AttendanceController {

    private final AttendanceService attendanceService;

    public AttendanceController(AttendanceService attendanceService) {
        this.attendanceService = attendanceService;
    }

    // Endpoint za upis podataka o prisustvu
    @PostMapping("/record")
    public ResponseEntity<Void> recordPresenceEvent(@RequestBody AttendanceRecord record) {
        attendanceService.recordPresence(record);
        return new ResponseEntity<>(HttpStatus.CREATED);
    }

    // 1. Analiza prosečne popunjenosti sale i predlog optimizacije
    @GetMapping("/analysis/room-utilization")
    public List<RoomUtilizationDTO> getRoomUtilization(
            @RequestParam(defaultValue = "30d") String range) {
        return attendanceService.getRoomUtilizationAnalysis(range);
    }

    // 2. Pronalaženje termina sa niskom posećenošću za spajanje
    @GetMapping("/analysis/low-attendance-periods")
    public List<LowAttendanceDTO> getLowAttendancePeriods(
            @RequestParam(defaultValue = "30d") String range,
            // Prag: npr. ako je manje od 20 studenata, posmatraj kao nisko
            @RequestParam(defaultValue = "20") double threshold) {
        return attendanceService.getLowAttendancePeriods(range, threshold);
    }

    // 3. Analiza maksimalnog opterećenja za kurs (za planiranje deobe)
    @GetMapping("/analysis/peak-load")
    public List<PeakLoadDTO> getPeakLoadForCourse(
            @RequestParam String courseCode, // Obavezan parametar
            @RequestParam(defaultValue = "90d") String range) {
        return attendanceService.getPeakLoadAnalysis(courseCode, range);
    }
}
