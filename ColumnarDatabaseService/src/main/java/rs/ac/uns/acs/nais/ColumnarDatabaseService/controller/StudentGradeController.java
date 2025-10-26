package rs.ac.uns.acs.nais.ColumnarDatabaseService.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import rs.ac.uns.acs.nais.ColumnarDatabaseService.dto.AnalyticsResponseDTO;
import rs.ac.uns.acs.nais.ColumnarDatabaseService.dto.StudentGradeDTO;
import rs.ac.uns.acs.nais.ColumnarDatabaseService.entity.StudentGrade;
import rs.ac.uns.acs.nais.ColumnarDatabaseService.service.StudentGradeService;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/v1/student-grades")
@CrossOrigin(origins = "*")
public class StudentGradeController {

    @Autowired
    private StudentGradeService studentGradeService;

    // === OSNOVNI CRUD ===

    @PostMapping
    public ResponseEntity<StudentGradeDTO> createGrade(@RequestBody StudentGradeDTO gradeDTO) {
        StudentGrade grade = convertToEntity(gradeDTO);
        StudentGrade savedGrade = studentGradeService.saveGrade(grade);
        return ResponseEntity.ok(convertToDTO(savedGrade));
    }

    @GetMapping
    public ResponseEntity<List<StudentGradeDTO>> getAllGrades() {
        List<StudentGrade> grades = studentGradeService.getAllGrades();
        List<StudentGradeDTO> gradeDTOs = grades.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
        return ResponseEntity.ok(gradeDTOs);
    }

    @GetMapping("/student/{studentId}")
    public ResponseEntity<List<StudentGradeDTO>> getGradesByStudent(@PathVariable Long studentId) {
        List<StudentGrade> grades = studentGradeService.getGradesByStudent(studentId);
        List<StudentGradeDTO> gradeDTOs = grades.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
        return ResponseEntity.ok(gradeDTOs);
    }

    // Read by composite key
    @GetMapping("/{studentId}/{academicYear}/{subjectId}/{examDate}")
    public ResponseEntity<StudentGradeDTO> getGradeByKey(
        @PathVariable Long studentId,
        @PathVariable String academicYear,
        @PathVariable String subjectId,
        @PathVariable String examDate // expected format: yyyy-MM-dd
    ) {
    // Decode path variable in case it contains encoded characters like '%2F'
    String academicYearDecoded = java.net.URLDecoder.decode(academicYear, java.nio.charset.StandardCharsets.UTF_8);
    LocalDateTime dt = LocalDate.parse(examDate).atStartOfDay();
    return studentGradeService.getGradeByKey(studentId, academicYearDecoded, subjectId, dt)
        .map(g -> ResponseEntity.ok(convertToDTO(g)))
        .orElse(ResponseEntity.notFound().build());
    }

    // UPDATE by composite key
    @PutMapping("/{studentId}/{academicYear}/{subjectId}/{examDate}")
    public ResponseEntity<StudentGradeDTO> updateGradeByKey(
        @PathVariable Long studentId,
        @PathVariable String academicYear,
        @PathVariable String subjectId,
        @PathVariable String examDate,
        @RequestBody StudentGradeDTO dto
    ) {
    String academicYearDecoded = java.net.URLDecoder.decode(academicYear, java.nio.charset.StandardCharsets.UTF_8);
    LocalDateTime dt = LocalDate.parse(examDate).atStartOfDay();
    StudentGrade updatedEntity = convertToEntity(dto);
    Optional<StudentGrade> saved = studentGradeService.updateGradeByKey(studentId, academicYearDecoded, subjectId, dt, updatedEntity);
    return saved.map(g -> ResponseEntity.ok(convertToDTO(g))).orElse(ResponseEntity.notFound().build());
    }

    // DELETE by composite key
    @DeleteMapping("/{studentId}/{academicYear}/{subjectId}/{examDate}")
    public ResponseEntity<Void> deleteGradeByKey(
        @PathVariable Long studentId,
        @PathVariable String academicYear,
        @PathVariable String subjectId,
        @PathVariable String examDate
    ) {
    String academicYearDecoded = java.net.URLDecoder.decode(academicYear, java.nio.charset.StandardCharsets.UTF_8);
    LocalDateTime dt = LocalDate.parse(examDate).atStartOfDay();
    boolean deleted = studentGradeService.deleteGradeByKey(studentId, academicYearDecoded, subjectId, dt);
    if (deleted) return ResponseEntity.noContent().build();
    return ResponseEntity.notFound().build();
    }

    // === ANALITIČKI ENDPOINTS ===

    @GetMapping("/analytics/student/{studentId}")
    public ResponseEntity<AnalyticsResponseDTO.StudentAnalysisDTO> getStudentAnalysis(@PathVariable Long studentId) {
        StudentGradeService.StudentAcademicAnalysis analysis = studentGradeService.analyzeStudentPerformance(studentId);
        
        AnalyticsResponseDTO.StudentAnalysisDTO dto = new AnalyticsResponseDTO.StudentAnalysisDTO(
                analysis.getStudentId(),
                analysis.getAverageGrade(),
                analysis.getPassedExams(),
                analysis.getFailedExams(),
                analysis.getSuccessRate()
        );
        
        return ResponseEntity.ok(dto);
    }

    @GetMapping("/analytics/top-students")
    public ResponseEntity<List<StudentGradeDTO>> getTopStudents(
            @RequestParam String academicYear,
            @RequestParam(defaultValue = "10") int limit) {
        
        List<StudentGrade> topStudents = studentGradeService.getTopStudentsByYear(academicYear, limit);
        List<StudentGradeDTO> studentDTOs = topStudents.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
        
        return ResponseEntity.ok(studentDTOs);
    }

    @GetMapping("/analytics/students-at-risk")
    public ResponseEntity<List<StudentGradeDTO>> getStudentsAtRisk() {
        List<StudentGrade> riskyStudents = studentGradeService.getStudentsAtRisk();
        List<StudentGradeDTO> studentDTOs = riskyStudents.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
        
        return ResponseEntity.ok(studentDTOs);
    }

    @GetMapping("/analytics/subject/{subjectId}")
    public ResponseEntity<AnalyticsResponseDTO.SubjectStatisticsDTO> getSubjectStatistics(
            @PathVariable String subjectId,
            @RequestParam String academicYear) {
        
        StudentGradeService.SubjectGradeStatistics stats = studentGradeService.getSubjectStatistics(subjectId, academicYear);
        
        AnalyticsResponseDTO.SubjectStatisticsDTO dto = new AnalyticsResponseDTO.SubjectStatisticsDTO(
                stats.getSubjectId(),
                "Subject Name", // Možeš dodati join sa Subject tabela
                "Department", // Možeš dodati join sa Subject tabela
                stats.getAcademicYear(),
                stats.getAverageGrade(),
                stats.getPassRate(),
                stats.getPassedCount() + stats.getFailedCount(),
                stats.getPassedCount(),
                stats.getFailedCount()
        );
        
        return ResponseEntity.ok(dto);
    }

    @GetMapping("/analytics/trends/{subjectId}")
    public ResponseEntity<Map<String, Double>> getSubjectTrends(@PathVariable String subjectId) {
        Map<String, Double> trends = studentGradeService.getAverageGradeTrends(subjectId);
        return ResponseEntity.ok(trends);
    }

    @GetMapping("/analytics/department/{department}")
    public ResponseEntity<AnalyticsResponseDTO.DepartmentAnalysisDTO> getDepartmentAnalysis(
            @PathVariable String department,
            @RequestParam String academicYear) {
        
        StudentGradeService.DepartmentAnalysis analysis = studentGradeService.getDepartmentPerformance(department, academicYear);
        
        AnalyticsResponseDTO.DepartmentAnalysisDTO dto = new AnalyticsResponseDTO.DepartmentAnalysisDTO(
                analysis.getDepartment(),
                analysis.getAcademicYear(),
                analysis.getAverageGrade(),
                (analysis.getPassedExams().doubleValue() / (analysis.getPassedExams() + analysis.getFailedExams()) * 100),
                analysis.getTotalStudents(),
                0 // Number of subjects - možeš dodati u analizu
        );
        
        return ResponseEntity.ok(dto);
    }

    @GetMapping("/analytics/professor/{professorId}")
    public ResponseEntity<AnalyticsResponseDTO.ProfessorEvaluationDTO> getProfessorAnalysis(
            @PathVariable Long professorId,
            @RequestParam String academicYear) {
        
        StudentGradeService.ProfessorGradeAnalysis analysis = studentGradeService.getProfessorGradeAnalysis(professorId, academicYear);
        
        AnalyticsResponseDTO.ProfessorEvaluationDTO dto = new AnalyticsResponseDTO.ProfessorEvaluationDTO(
                analysis.getProfessorId(),
                analysis.getAcademicYear(),
                analysis.getPassRate() >= 80.0 ? "EXCELLENT" : analysis.getPassRate() >= 60.0 ? "GOOD" : "NEEDS_IMPROVEMENT",
                analysis.getPassRate(),
                analysis.getPassRate(),
                analysis.getAverageGrade(),
                "High student success rate", // Možeš proširiti analizu
                "Continue current approach" // Možeš proširiti analizu
        );
        
        return ResponseEntity.ok(dto);
    }

    @GetMapping("/analytics/date-range")
    public ResponseEntity<List<StudentGradeDTO>> getGradesByDateRange(
            @RequestParam String startDate,
            @RequestParam String endDate) {
        
        LocalDate start = LocalDate.parse(startDate);
        LocalDate end = LocalDate.parse(endDate);
        
        List<StudentGrade> grades = studentGradeService.getGradesByDateRange(start, end);
        List<StudentGradeDTO> gradeDTOs = grades.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
        
        return ResponseEntity.ok(gradeDTOs);
    }

    // === HELPER METHODS ===

    private StudentGrade convertToEntity(StudentGradeDTO dto) {
        StudentGrade grade = new StudentGrade();
        grade.setStudentId(dto.getStudentId());
        grade.setSubjectId(dto.getSubjectId());
        grade.setAcademicYear(dto.getAcademicYear());
        // Convert LocalDate to LocalDateTime
        grade.setExamDate(dto.getExamDate().atStartOfDay());
        grade.setGrade(dto.getGrade());
        grade.setExamType(dto.getExamType());
        grade.setProfessorId(dto.getProfessorId());
        grade.setDepartment(dto.getDepartment());
        return grade;
    }

    private StudentGradeDTO convertToDTO(StudentGrade grade) {
        return new StudentGradeDTO(
                grade.getStudentId(),
                grade.getSubjectId(),
                grade.getAcademicYear(),
                // Convert LocalDateTime to LocalDate
                grade.getExamDate().toLocalDate(),
                grade.getGrade(),
                grade.getExamType(),
                grade.getProfessorId(),
                grade.getDepartment()
        );
    }
}