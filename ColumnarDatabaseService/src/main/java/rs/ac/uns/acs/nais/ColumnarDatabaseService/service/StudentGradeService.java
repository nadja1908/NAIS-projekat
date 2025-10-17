package rs.ac.uns.acs.nais.ColumnarDatabaseService.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import rs.ac.uns.acs.nais.ColumnarDatabaseService.entity.StudentGrade;
import rs.ac.uns.acs.nais.ColumnarDatabaseService.repository.StudentGradeRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class StudentGradeService {

    @Autowired
    private StudentGradeRepository studentGradeRepository;

    // === OSNOVNI CRUD ===
    
    public StudentGrade saveGrade(StudentGrade grade) {
        return studentGradeRepository.save(grade);
    }
    
    public List<StudentGrade> getAllGrades() {
        return studentGradeRepository.findAll();
    }
    
    public List<StudentGrade> getGradesByStudent(Long studentId) {
        return studentGradeRepository.findByStudentId(studentId);
    }

    // === ANALITIČKI SERVISI ===

    /**
     * Analiza akademskog uspеха studenta
     */
    public StudentAcademicAnalysis analyzeStudentPerformance(Long studentId) {
        List<StudentGrade> grades = studentGradeRepository.findByStudentId(studentId);
        
        if (grades.isEmpty()) {
            return new StudentAcademicAnalysis(studentId, 0.0, 0, 0, 0.0);
        }

        // Prosečna ocena
        Double averageGrade = studentGradeRepository.getAverageGradeByStudent(studentId);
        
        // Broj položenih
        Long passedCount = studentGradeRepository.getPassedGradesCountByStudent(studentId);
        
        // Broj pala
        Long failedCount = studentGradeRepository.getFailedGradesCountByStudent(studentId);
        
        // Procenat uspešnosti
        double successRate = passedCount.doubleValue() / (passedCount + failedCount) * 100;
        
        return new StudentAcademicAnalysis(studentId, averageGrade, 
                                         passedCount.intValue(), failedCount.intValue(), successRate);
    }

    /**
     * Top studenti po proseku za godinu
     */
    public List<StudentGrade> getTopStudentsByYear(String academicYear, int limit) {
        return studentGradeRepository.findByAcademicYear(academicYear)
                .stream()
                .collect(Collectors.groupingBy(StudentGrade::getStudentId))
                .entrySet()
                .stream()
                .map(entry -> {
                    List<StudentGrade> studentGrades = entry.getValue();
                    double avgGrade = studentGrades.stream()
                            .mapToDouble(StudentGrade::getGrade)
                            .average()
                            .orElse(0.0);
                    
                    StudentGrade representative = studentGrades.get(0);
                    representative.setGrade(avgGrade); // Privremeno za prikaz proseka
                    return representative;
                })
                .sorted((a, b) -> Double.compare(b.getGrade(), a.getGrade()))
                .limit(limit)
                .collect(Collectors.toList());
    }

    /**
     * Studenti u riziku (prosek ispod 6.0)
     */
    public List<StudentGrade> getStudentsAtRisk() {
        return studentGradeRepository.findStudentsWithLowGrades(6.0);
    }

    /**
     * Statistike po predmetu
     */
    public SubjectGradeStatistics getSubjectStatistics(String subjectId, String academicYear) {
        List<StudentGrade> grades = studentGradeRepository.findBySubjectIdAndYear(subjectId, academicYear);
        
        if (grades.isEmpty()) {
            return new SubjectGradeStatistics(subjectId, academicYear, 0.0, 0, 0, 0.0);
        }

        Double avgGrade = grades.stream()
                .mapToDouble(StudentGrade::getGrade)
                .average()
                .orElse(0.0);

        long passedCount = grades.stream()
                .filter(g -> g.getGrade() >= 6.0)
                .count();

        long failedCount = grades.size() - passedCount;
        
        double passRate = (passedCount * 100.0) / grades.size();

        return new SubjectGradeStatistics(subjectId, academicYear, avgGrade, 
                                        (int)passedCount, (int)failedCount, passRate);
    }

    /**
     * Trend analize - poređenje kroz godine
     */
    public Map<String, Double> getAverageGradeTrends(String subjectId) {
        List<StudentGrade> allGrades = studentGradeRepository.findBySubjectId(subjectId);
        
        return allGrades.stream()
                .collect(Collectors.groupingBy(
                    StudentGrade::getAcademicYear,
                    Collectors.averagingDouble(StudentGrade::getGrade)
                ));
    }

    /**
     * Departmentske statistike
     */
    public DepartmentAnalysis getDepartmentPerformance(String department, String academicYear) {
        List<StudentGrade> grades = studentGradeRepository.findByDepartmentAndYear(department, academicYear);
        
        if (grades.isEmpty()) {
            return new DepartmentAnalysis(department, academicYear, 0.0, 0, 0, 0);
        }

        Double avgGrade = grades.stream()
                .mapToDouble(StudentGrade::getGrade)
                .average()
                .orElse(0.0);

        long passedCount = grades.stream()
                .filter(g -> g.getGrade() >= 6.0)
                .count();

        long failedCount = grades.size() - passedCount;
        
        long totalStudents = grades.stream()
                .map(StudentGrade::getStudentId)
                .distinct()
                .count();

        return new DepartmentAnalysis(department, academicYear, avgGrade, 
                                    (int)passedCount, (int)failedCount, (int)totalStudents);
    }

    /**
     * Profesorske analize
     */
    public ProfessorGradeAnalysis getProfessorGradeAnalysis(Long professorId, String academicYear) {
        List<StudentGrade> grades = studentGradeRepository.findByProfessorIdAndYear(professorId, academicYear);
        
        if (grades.isEmpty()) {
            return new ProfessorGradeAnalysis(professorId, academicYear, 0.0, 0.0, 0, 0);
        }

        Double avgGrade = grades.stream()
                .mapToDouble(StudentGrade::getGrade)
                .average()
                .orElse(0.0);

        long passedCount = grades.stream()
                .filter(g -> g.getGrade() >= 6.0)
                .count();

        double passRate = (passedCount * 100.0) / grades.size();
        
        long totalStudents = grades.stream()
                .map(StudentGrade::getStudentId)
                .distinct()
                .count();

        return new ProfessorGradeAnalysis(professorId, academicYear, avgGrade, 
                                       passRate, (int)passedCount, (int)totalStudents);
    }

    /**
     * Vremenski period analize
     */
    public List<StudentGrade> getGradesByDateRange(LocalDate startDate, LocalDate endDate) {
        // Convert LocalDate to LocalDateTime
        return studentGradeRepository.findByExamDateBetween(
            startDate.atStartOfDay(), 
            endDate.atTime(23, 59, 59)
        );
    }

    // === HELPER KLASE ===
    
    public static class StudentAcademicAnalysis {
        private Long studentId;
        private Double averageGrade;
        private Integer passedExams;
        private Integer failedExams;
        private Double successRate;

        public StudentAcademicAnalysis(Long studentId, Double averageGrade, 
                                     Integer passedExams, Integer failedExams, Double successRate) {
            this.studentId = studentId;
            this.averageGrade = averageGrade;
            this.passedExams = passedExams;
            this.failedExams = failedExams;
            this.successRate = successRate;
        }

        // Getters
        public Long getStudentId() { return studentId; }
        public Double getAverageGrade() { return averageGrade; }
        public Integer getPassedExams() { return passedExams; }
        public Integer getFailedExams() { return failedExams; }
        public Double getSuccessRate() { return successRate; }
    }

    public static class SubjectGradeStatistics {
        private String subjectId;
        private String academicYear;
        private Double averageGrade;
        private Integer passedCount;
        private Integer failedCount;
        private Double passRate;

        public SubjectGradeStatistics(String subjectId, String academicYear, Double averageGrade,
                                    Integer passedCount, Integer failedCount, Double passRate) {
            this.subjectId = subjectId;
            this.academicYear = academicYear;
            this.averageGrade = averageGrade;
            this.passedCount = passedCount;
            this.failedCount = failedCount;
            this.passRate = passRate;
        }

        // Getters
        public String getSubjectId() { return subjectId; }
        public String getAcademicYear() { return academicYear; }
        public Double getAverageGrade() { return averageGrade; }
        public Integer getPassedCount() { return passedCount; }
        public Integer getFailedCount() { return failedCount; }
        public Double getPassRate() { return passRate; }
    }

    public static class DepartmentAnalysis {
        private String department;
        private String academicYear;
        private Double averageGrade;
        private Integer passedExams;
        private Integer failedExams;
        private Integer totalStudents;

        public DepartmentAnalysis(String department, String academicYear, Double averageGrade,
                                Integer passedExams, Integer failedExams, Integer totalStudents) {
            this.department = department;
            this.academicYear = academicYear;
            this.averageGrade = averageGrade;
            this.passedExams = passedExams;
            this.failedExams = failedExams;
            this.totalStudents = totalStudents;
        }

        // Getters
        public String getDepartment() { return department; }
        public String getAcademicYear() { return academicYear; }
        public Double getAverageGrade() { return averageGrade; }
        public Integer getPassedExams() { return passedExams; }
        public Integer getFailedExams() { return failedExams; }
        public Integer getTotalStudents() { return totalStudents; }
    }

    public static class ProfessorGradeAnalysis {
        private Long professorId;
        private String academicYear;
        private Double averageGrade;
        private Double passRate;
        private Integer passedStudents;
        private Integer totalStudents;

        public ProfessorGradeAnalysis(Long professorId, String academicYear, Double averageGrade,
                                    Double passRate, Integer passedStudents, Integer totalStudents) {
            this.professorId = professorId;
            this.academicYear = academicYear;
            this.averageGrade = averageGrade;
            this.passRate = passRate;
            this.passedStudents = passedStudents;
            this.totalStudents = totalStudents;
        }

        // Getters
        public Long getProfessorId() { return professorId; }
        public String getAcademicYear() { return academicYear; }
        public Double getAverageGrade() { return averageGrade; }
        public Double getPassRate() { return passRate; }
        public Integer getPassedStudents() { return passedStudents; }
        public Integer getTotalStudents() { return totalStudents; }
    }
}