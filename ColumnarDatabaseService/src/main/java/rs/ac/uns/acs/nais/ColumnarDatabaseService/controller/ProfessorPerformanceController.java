package rs.ac.uns.acs.nais.ColumnarDatabaseService.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import rs.ac.uns.acs.nais.ColumnarDatabaseService.service.ProfessorPerformanceService;

import java.util.List;
import java.util.Map;
import java.util.HashMap;
import java.util.ArrayList;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/v1/professor-performance")
@CrossOrigin(origins = "*")
@Tag(name = "ProfessorPerformance", description = "CRUD and analytics for professor performance")
public class ProfessorPerformanceController {

    @Autowired
    private ProfessorPerformanceService service;

    @Operation(summary = "Top professors by pass rate")
    @GetMapping("/top")
    public ResponseEntity<List<Map<String, Object>>> getTop(@RequestParam String academicYear, @RequestParam(defaultValue = "10") int limit) {
        List<ProfessorPerformanceService.ProfessorRanking> rankings = service.getTopProfessorsByPassRate(academicYear, limit);
    List<Map<String,Object>> out = rankings.stream().map(r -> {
        Map<String,Object> m = new HashMap<>();
        m.put("professorId", r.getProfessorId());
        m.put("passRate", r.getPassRate());
        m.put("averageGrade", r.getAverageGrade());
        m.put("totalStudents", r.getTotalStudents());
        m.put("category", r.getCategory());
        return m;
    }).collect(Collectors.toList());
        return ResponseEntity.ok(out);
    }

    @Operation(summary = "Get performances for a professor")
    @GetMapping("/{professorId}")
    public ResponseEntity<List<Map<String,Object>>> getByProfessor(@PathVariable Long professorId) {
        List<?> list = service.getPerformancesByProfessor(professorId);
        // map ProfessorPerformance objects to simple maps for JSON serialization
        List<Map<String,Object>> out = new ArrayList<>();
        for (Object p : list) {
            Map<String,Object> m = new HashMap<>();
            try {
                // assume p is ProfessorPerformance
                java.lang.reflect.Method getProfessorId = p.getClass().getMethod("getProfessorId");
                java.lang.reflect.Method getAcademicYear = p.getClass().getMethod("getAcademicYear");
                java.lang.reflect.Method getDepartment = p.getClass().getMethod("getDepartment");
                java.lang.reflect.Method getOverallPassRate = p.getClass().getMethod("getOverallPassRate");
                java.lang.reflect.Method getAverageGradeGiven = p.getClass().getMethod("getAverageGradeGiven");
                m.put("professorId", getProfessorId.invoke(p));
                m.put("academicYear", getAcademicYear.invoke(p));
                m.put("department", getDepartment.invoke(p));
                m.put("overallPassRate", getOverallPassRate.invoke(p));
                m.put("averageGradeGiven", getAverageGradeGiven.invoke(p));
            } catch (Exception e) {
                m.put("performance", p);
            }
            out.add(m);
        }
        return ResponseEntity.ok(out);
    }
}
