package rs.ac.uns.acs.nais.ColumnarDatabaseService.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import rs.ac.uns.acs.nais.ColumnarDatabaseService.entity.GradesBySubjectYear;
import rs.ac.uns.acs.nais.ColumnarDatabaseService.service.GradesBySubjectYearService;

import java.util.List;

@RestController
@RequestMapping("/api/v1/grades-by-subject")
@CrossOrigin(origins = "*")
@Tag(name = "GradesBySubjectYear", description = "Query-driven access to grades by subject and year")
public class GradesBySubjectYearController {

    @Autowired
    private GradesBySubjectYearService service;

    @Operation(summary = "Create a denormalized grade row")
    @PostMapping
    public ResponseEntity<GradesBySubjectYear> create(@RequestBody GradesBySubjectYear row) {
        return ResponseEntity.ok(service.save(row));
    }

    @Operation(summary = "List grades for subject in a year")
    @GetMapping("/{subjectId}")
    public ResponseEntity<List<GradesBySubjectYear>> getBySubjectAndYear(
            @PathVariable String subjectId,
            @RequestParam String academicYear) {
        return ResponseEntity.ok(service.findBySubjectAndYear(subjectId, academicYear));
    }

    @Operation(summary = "Average grade for subject in a year")
    @GetMapping("/{subjectId}/avg")
    public ResponseEntity<Double> getAverage(@PathVariable String subjectId, @RequestParam String academicYear) {
        return ResponseEntity.ok(service.getAverageGrade(subjectId, academicYear));
    }
}
