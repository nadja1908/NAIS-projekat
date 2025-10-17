package rs.ac.uns.acs.nais.ColumnarDatabaseService.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import rs.ac.uns.acs.nais.ColumnarDatabaseService.dto.AnalyticsResponseDTO;
import rs.ac.uns.acs.nais.ColumnarDatabaseService.dto.StudentDTO;
import rs.ac.uns.acs.nais.ColumnarDatabaseService.dto.SubjectDTO;
import rs.ac.uns.acs.nais.ColumnarDatabaseService.entity.Student;
import rs.ac.uns.acs.nais.ColumnarDatabaseService.entity.Subject;
import rs.ac.uns.acs.nais.ColumnarDatabaseService.service.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/v1")
@CrossOrigin(origins = "*")
public class MainController {

    @Autowired
    private StudentService studentService;

    @Autowired
    private SubjectService subjectService;

    @Autowired
    private StudentGradeService studentGradeService;

    @Autowired
    private SubjectStatisticsService subjectStatisticsService;

    @Autowired
    private ProfessorPerformanceService professorPerformanceService;

    // === DASHBOARD ENDPOINT ===
    
    @GetMapping("/dashboard")
    public ResponseEntity<Map<String, Object>> getDashboard() {
        Map<String, Object> dashboard = new HashMap<>();
        
        // Osnovne statistike
        dashboard.put("totalStudents", studentService.getTotalStudentsCount());
        dashboard.put("activeStudents", studentService.getActiveStudentsCount());
        dashboard.put("totalSubjects", subjectService.getTotalSubjectsCount());
        
        // Quick insights
        List<String> insights = List.of(
            "Student Success Analysis System",
            "University Analytics Platform",
            "Powered by Cassandra Database",
            "NAIS Project - Team IN_G1_TIM6"
        );
        dashboard.put("systemInfo", insights);
        
        return ResponseEntity.ok(dashboard);
    }

    // === STUDENT ENDPOINTS ===
    
    @GetMapping("/students")
    public ResponseEntity<List<StudentDTO>> getAllStudents() {
        List<Student> students = studentService.getAllStudents();
        List<StudentDTO> studentDTOs = students.stream()
                .map(this::convertStudentToDTO)
                .collect(Collectors.toList());
        return ResponseEntity.ok(studentDTOs);
    }

    @GetMapping("/students/active")
    public ResponseEntity<List<StudentDTO>> getActiveStudents() {
        List<Student> students = studentService.getActiveStudents();
        List<StudentDTO> studentDTOs = students.stream()
                .map(this::convertStudentToDTO)
                .collect(Collectors.toList());
        return ResponseEntity.ok(studentDTOs);
    }

    @GetMapping("/students/program/{program}")
    public ResponseEntity<List<StudentDTO>> getStudentsByProgram(@PathVariable String program) {
        List<Student> students = studentService.getStudentsByProgram(program);
        List<StudentDTO> studentDTOs = students.stream()
                .map(this::convertStudentToDTO)
                .collect(Collectors.toList());
        return ResponseEntity.ok(studentDTOs);
    }

    @PostMapping("/students")
    public ResponseEntity<StudentDTO> createStudent(@RequestBody StudentDTO studentDTO) {
        Student student = convertDTOToStudent(studentDTO);
        Student savedStudent = studentService.saveStudent(student);
        return ResponseEntity.ok(convertStudentToDTO(savedStudent));
    }

    @GetMapping("/students/{studentId}")
    public ResponseEntity<StudentDTO> getStudentById(@PathVariable Long studentId) {
        return studentService.getStudentById(studentId)
                .map(student -> ResponseEntity.ok(convertStudentToDTO(student)))
                .orElse(ResponseEntity.notFound().build());
    }

    // === SUBJECT ENDPOINTS ===
    
    @GetMapping("/subjects")
    public ResponseEntity<List<SubjectDTO>> getAllSubjects() {
        List<Subject> subjects = subjectService.getAllSubjects();
        List<SubjectDTO> subjectDTOs = subjects.stream()
                .map(this::convertSubjectToDTO)
                .collect(Collectors.toList());
        return ResponseEntity.ok(subjectDTOs);
    }

    @GetMapping("/subjects/department/{department}")
    public ResponseEntity<List<SubjectDTO>> getSubjectsByDepartment(@PathVariable String department) {
        List<Subject> subjects = subjectService.getSubjectsByDepartment(department);
        List<SubjectDTO> subjectDTOs = subjects.stream()
                .map(this::convertSubjectToDTO)
                .collect(Collectors.toList());
        return ResponseEntity.ok(subjectDTOs);
    }

    @PostMapping("/subjects")
    public ResponseEntity<SubjectDTO> createSubject(@RequestBody SubjectDTO subjectDTO) {
        Subject subject = convertDTOToSubject(subjectDTO);
        Subject savedSubject = subjectService.saveSubject(subject);
        return ResponseEntity.ok(convertSubjectToDTO(savedSubject));
    }

    @GetMapping("/subjects/{subjectId}")
    public ResponseEntity<SubjectDTO> getSubjectById(@PathVariable String subjectId) {
        return subjectService.getSubjectById(subjectId)
                .map(subject -> ResponseEntity.ok(convertSubjectToDTO(subject)))
                .orElse(ResponseEntity.notFound().build());
    }

    // === ADVANCED ANALYTICS ===
    
    @GetMapping("/analytics/department-comparison")
    public ResponseEntity<List<AnalyticsResponseDTO.DepartmentAnalysisDTO>> compareDepartments(
            @RequestParam String academicYear) {
        
        List<SubjectStatisticsService.DepartmentComparison> comparisons = 
                subjectStatisticsService.compareDepartments(academicYear);
        
        List<AnalyticsResponseDTO.DepartmentAnalysisDTO> dtos = comparisons.stream()
                .map(comp -> new AnalyticsResponseDTO.DepartmentAnalysisDTO(
                        comp.getDepartment(),
                        academicYear,
                        comp.getAverageGrade(),
                        comp.getAveragePassRate(),
                        comp.getTotalStudents(),
                        comp.getTotalSubjects()
                ))
                .collect(Collectors.toList());
        
        return ResponseEntity.ok(dtos);
    }

    @GetMapping("/analytics/professor-rankings")
    public ResponseEntity<List<Map<String, Object>>> getProfessorRankings(
            @RequestParam String academicYear,
            @RequestParam(defaultValue = "10") int limit) {
        
        List<ProfessorPerformanceService.ProfessorRanking> rankings = 
                professorPerformanceService.getTopProfessorsByPassRate(academicYear, limit);
        
        List<Map<String, Object>> rankingDtos = rankings.stream()
                .map(ranking -> {
                    Map<String, Object> dto = new HashMap<>();
                    dto.put("professorId", ranking.getProfessorId());
                    dto.put("passRate", ranking.getPassRate());
                    dto.put("averageGrade", ranking.getAverageGrade());
                    dto.put("totalStudents", ranking.getTotalStudents());
                    dto.put("category", ranking.getCategory());
                    return dto;
                })
                .collect(Collectors.toList());
        
        return ResponseEntity.ok(rankingDtos);
    }

    @GetMapping("/analytics/difficult-subjects")
    public ResponseEntity<List<Map<String, Object>>> getDifficultSubjects(
            @RequestParam String department) {
        
        List<rs.ac.uns.acs.nais.ColumnarDatabaseService.entity.SubjectStatistics> difficultSubjects = 
                subjectStatisticsService.getCriticalSubjects(department);
        
        List<Map<String, Object>> subjectDtos = difficultSubjects.stream()
                .map(subject -> {
                    Map<String, Object> dto = new HashMap<>();
                    dto.put("subjectId", subject.getSubjectId());
                    dto.put("department", subject.getDepartment());
                    dto.put("passRate", subject.getPassRatePercentage());
                    dto.put("averageGrade", subject.getAverageGrade());
                    dto.put("totalStudents", subject.getTotalEnrolledStudents());
                    dto.put("academicYear", subject.getAcademicYear());
                    return dto;
                })
                .collect(Collectors.toList());
        
        return ResponseEntity.ok(subjectDtos);
    }

    @GetMapping("/analytics/trends/{subjectId}")
    public ResponseEntity<Map<String, Object>> getSubjectTrendAnalysis(
            @PathVariable String subjectId,
            @RequestParam String department) {
        
        SubjectStatisticsService.SubjectTrendAnalysis trend = 
                subjectStatisticsService.analyzeSubjectTrends(department, subjectId);
        
        Map<String, Object> trendDto = new HashMap<>();
        trendDto.put("subjectId", trend.getSubjectId());
        trendDto.put("trendDirection", trend.getTrendDirection());
        trendDto.put("passRateChange", trend.getPassRateChange());
        trendDto.put("gradeChange", trend.getGradeChange());
        
        return ResponseEntity.ok(trendDto);
    }

    // === CONVERSION HELPERS ===
    
    private StudentDTO convertStudentToDTO(Student student) {
        return new StudentDTO(
                student.getStudentId(),
                student.getFirstName(),
                student.getLastName(),
                student.getIndexNumber(),
                student.getStudyProgram(),
                student.getCurrentYearOfStudy(),
                student.getEnrollmentDate(),
                student.isActive()
        );
    }

    private Student convertDTOToStudent(StudentDTO dto) {
        Student student = new Student();
        student.setStudentId(dto.getStudentId());
        student.setFirstName(dto.getFirstName());
        student.setLastName(dto.getLastName());
        student.setIndexNumber(dto.getIndexNumber());
        student.setStudyProgram(dto.getStudyProgram());
        student.setCurrentYearOfStudy(dto.getCurrentYearOfStudy());
        student.setEnrollmentDate(dto.getEnrollmentDate());
        student.setActive(dto.getIsActive());
        return student;
    }

    private SubjectDTO convertSubjectToDTO(Subject subject) {
        return new SubjectDTO(
                subject.getSubjectId(),
                subject.getSubjectName(),
                subject.getDepartment(),
                subject.getYearOfStudy(),
                subject.getSemester(),
                subject.getEctsPoints(),
                subject.isMandatory(),
                subject.getProfessorId()
        );
    }

    private Subject convertDTOToSubject(SubjectDTO dto) {
        Subject subject = new Subject();
        subject.setSubjectId(dto.getSubjectId());
        subject.setSubjectName(dto.getSubjectName());
        subject.setDepartment(dto.getDepartment());
        subject.setYearOfStudy(dto.getYearOfStudy());
        subject.setSemester(dto.getSemester());
        subject.setEctsPoints(dto.getEctsPoints());
        subject.setMandatory(dto.getIsMandatory());
        subject.setProfessorId(dto.getProfessorId());
        return subject;
    }
}