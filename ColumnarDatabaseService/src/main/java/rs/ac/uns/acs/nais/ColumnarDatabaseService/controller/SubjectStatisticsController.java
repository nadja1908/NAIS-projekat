package rs.ac.uns.acs.nais.ColumnarDatabaseService.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import rs.ac.uns.acs.nais.ColumnarDatabaseService.entity.SubjectStatistics;
import rs.ac.uns.acs.nais.ColumnarDatabaseService.service.SubjectStatisticsService;

import java.util.List;

@RestController
@RequestMapping("/api/v1/subject-statistics")
@CrossOrigin(origins = "*")
@Tag(name = "SubjectStatistics", description = "CRUD and analytics for subject statistics")
public class SubjectStatisticsController {

    @Autowired
    private SubjectStatisticsService subjectStatisticsService;

    // Create (or upsert)
    @Operation(summary = "Create or upsert subject statistics", description = "Create a new SubjectStatistics record or update an existing one.")
    @RequestBody(
        description = "SubjectStatistics JSON",
        required = true,
        content = @Content(mediaType = "application/json",
                schema = @Schema(implementation = SubjectStatistics.class),
                examples = @ExampleObject(value = "{\"department\":\"FTN\",\"academicYear\":\"2024/2025\",\"subjectId\":\"CS101\",\"subjectName\":\"Algorithms\",\"professorId\":123,\"totalEnrolledStudents\":120,\"totalPassedStudents\":90,\"totalFailedStudents\":30,\"passRatePercentage\":75.0,\"averageGrade\":8.2,\"totalAttempts\":150,\"yearOfStudy\":2,\"semester\":3,\"ectsPoints\":6}"))
    )
    @PostMapping
    public ResponseEntity<SubjectStatistics> createStatistics(@org.springframework.web.bind.annotation.RequestBody SubjectStatistics stats) {
        SubjectStatistics saved = subjectStatisticsService.saveStatistics(stats);
        return ResponseEntity.ok(saved);
    }

    // Read all
    @Operation(summary = "List subject statistics", description = "Get all statistics or filter by department")
    @GetMapping
    public ResponseEntity<List<SubjectStatistics>> getAllStatistics(@RequestParam(required = false) String department) {
        if (department != null && !department.isEmpty()) {
            return ResponseEntity.ok(subjectStatisticsService.getStatisticsByDepartment(department));
        }
        return ResponseEntity.ok(subjectStatisticsService.getAllStatistics());
    }

    // Read by composite key
    @Operation(summary = "Get statistics by composite key", description = "Get statistics for a specific department / academic year / subject")
    @GetMapping("/{department}/{academicYear}/{subjectId}")
    public ResponseEntity<SubjectStatistics> getStatisticsByKey(
            @PathVariable String department,
            @PathVariable String academicYear,
            @PathVariable String subjectId) {

        SubjectStatistics stats = subjectStatisticsService.getStatistics(subjectId, department, academicYear);
        if (stats == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(stats);
    }

    // Update (upsert) - client may also just call POST to upsert
    @Operation(summary = "Update subject statistics", description = "Update (or upsert) the subject statistics for the composite key")
    @RequestBody(
        description = "Updated SubjectStatistics JSON",
        required = true,
        content = @Content(mediaType = "application/json",
                schema = @Schema(implementation = SubjectStatistics.class),
                examples = @ExampleObject(value = "{\"department\":\"FTN\",\"academicYear\":\"2024/2025\",\"subjectId\":\"CS101\",\"subjectName\":\"Algorithms\",\"professorId\":123,\"totalEnrolledStudents\":125,\"totalPassedStudents\":95,\"totalFailedStudents\":30,\"passRatePercentage\":76.0,\"averageGrade\":8.3,\"totalAttempts\":155,\"yearOfStudy\":2,\"semester\":3,\"ectsPoints\":6}"))
    )
    @PutMapping("/{department}/{academicYear}/{subjectId}")
    public ResponseEntity<SubjectStatistics> updateStatistics(
            @PathVariable String department,
            @PathVariable String academicYear,
            @PathVariable String subjectId,
            @org.springframework.web.bind.annotation.RequestBody SubjectStatistics updated) {

        // Ensure composite key fields are set consistently
        updated.setDepartment(department);
        updated.setAcademicYear(academicYear);
        updated.setSubjectId(subjectId);

        SubjectStatistics saved = subjectStatisticsService.saveStatistics(updated);
        return ResponseEntity.ok(saved);
    }

    // Delete
    @Operation(summary = "Delete subject statistics", description = "Delete a SubjectStatistics row by composite key")
    @DeleteMapping("/{department}/{academicYear}/{subjectId}")
    public ResponseEntity<Void> deleteStatistics(
            @PathVariable String department,
            @PathVariable String academicYear,
            @PathVariable String subjectId) {

        boolean deleted = subjectStatisticsService.deleteStatistics(subjectId, department, academicYear);
        if (deleted) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.notFound().build();
    }
}
