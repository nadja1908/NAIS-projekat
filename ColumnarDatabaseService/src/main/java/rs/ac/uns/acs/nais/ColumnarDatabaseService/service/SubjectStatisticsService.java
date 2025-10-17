package rs.ac.uns.acs.nais.ColumnarDatabaseService.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import rs.ac.uns.acs.nais.ColumnarDatabaseService.entity.SubjectStatistics;
import rs.ac.uns.acs.nais.ColumnarDatabaseService.repository.SubjectStatisticsRepository;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class SubjectStatisticsService {

    @Autowired
    private SubjectStatisticsRepository subjectStatisticsRepository;

    // === OSNOVNI CRUD ===
    
    public SubjectStatistics saveStatistics(SubjectStatistics statistics) {
        return subjectStatisticsRepository.save(statistics);
    }

    public List<SubjectStatistics> getAllStatistics() {
        return subjectStatisticsRepository.findAll();
    }

    public List<SubjectStatistics> getStatisticsByDepartment(String department) {
        return subjectStatisticsRepository.findByDepartment(department);
    }

    // === DEPARTMENTSKE ANALIZE ===

    /**
     * Kompletna analiza departmana za godinu
     */
    public DepartmentComprehensiveAnalysis analyzeDepartment(String department, String academicYear) {
        List<SubjectStatistics> stats = subjectStatisticsRepository.findByDepartmentAndYear(department, academicYear);
        
        if (stats.isEmpty()) {
            return new DepartmentComprehensiveAnalysis(department, academicYear, 0.0, 0.0, 0, 0, "N/A");
        }

        // Prosečna prolaznost departmana
        Double avgPassRate = stats.stream()
                .mapToDouble(SubjectStatistics::getPassRatePercentage)
                .average()
                .orElse(0.0);

        // Prosečna ocena departmana
        Double avgGrade = stats.stream()
                .mapToDouble(SubjectStatistics::getAverageGrade)
                .average()
                .orElse(0.0);

        // Ukupan broj studenata
        Integer totalStudents = stats.stream()
                .mapToInt(SubjectStatistics::getTotalEnrolledStudents)
                .sum();

        // Broj predmeta
        Integer totalSubjects = stats.size();

        // Najteži predmet (najniža prolaznost)
        SubjectStatistics hardestSubject = stats.stream()
                .min(Comparator.comparing(SubjectStatistics::getPassRatePercentage))
                .orElse(null);

        return new DepartmentComprehensiveAnalysis(department, academicYear, avgPassRate, 
                                                 avgGrade, totalStudents, totalSubjects, 
                                                 hardestSubject != null ? hardestSubject.getSubjectId() : "N/A");
    }

    /**
     * Top 10 najlakših predmeta po departmanu
     */
    public List<SubjectStatistics> getEasiestSubjects(String department, String academicYear, int limit) {
        return subjectStatisticsRepository.findByDepartmentAndYear(department, academicYear)
                .stream()
                .sorted(Comparator.comparing(SubjectStatistics::getPassRatePercentage).reversed())
                .limit(limit)
                .collect(Collectors.toList());
    }

    /**
     * Top 10 najteži predmeta po departmanu
     */
    public List<SubjectStatistics> getHardestSubjects(String department, String academicYear, int limit) {
        return subjectStatisticsRepository.findByDepartmentAndYear(department, academicYear)
                .stream()
                .sorted(Comparator.comparing(SubjectStatistics::getPassRatePercentage))
                .limit(limit)
                .collect(Collectors.toList());
    }

    /**
     * Predmeti sa kritično niskom prolaznosću (< 40%)
     */
    public List<SubjectStatistics> getCriticalSubjects(String department) {
        return subjectStatisticsRepository.findSubjectsWithLowPassRate(department, 40.0);
    }

    /**
     * Predmeti sa visokim prosečnim ocenama
     */
    public List<SubjectStatistics> getHighPerformingSubjects(String department, double gradeThreshold) {
        return subjectStatisticsRepository.findSubjectsWithHighAverageGrade(department, gradeThreshold);
    }

    // === TREND ANALIZE ===

    /**
     * Analiza trenda predmeta kroz godine
     */
    public SubjectTrendAnalysis analyzeSubjectTrends(String department, String subjectId) {
        List<SubjectStatistics> trends = subjectStatisticsRepository.findSubjectTrendsOverYears(department, subjectId);
        
        if (trends.size() < 2) {
            return new SubjectTrendAnalysis(subjectId, "INSUFFICIENT_DATA", 0.0, 0.0);
        }

        // Sortiranje po godinama
        trends.sort(Comparator.comparing(SubjectStatistics::getAcademicYear));

        SubjectStatistics oldest = trends.get(0);
        SubjectStatistics newest = trends.get(trends.size() - 1);

        // Trend prolaznosti
        double passRateChange = newest.getPassRatePercentage() - oldest.getPassRatePercentage();
        double gradeChange = newest.getAverageGrade() - oldest.getAverageGrade();

        String trendDirection;
        if (passRateChange > 5.0) {
            trendDirection = "IMPROVING";
        } else if (passRateChange < -5.0) {
            trendDirection = "DECLINING";
        } else {
            trendDirection = "STABLE";
        }

        return new SubjectTrendAnalysis(subjectId, trendDirection, passRateChange, gradeChange);
    }

    /**
     * Poređenje departmana za godinu
     */
    public List<DepartmentComparison> compareDepartments(String academicYear) {
        // Skupi sve statistike za godinu
        List<SubjectStatistics> allStats = subjectStatisticsRepository.findAllSubjectStatisticsByYear(academicYear);
        
        // Grupiši po departmanima
        Map<String, List<SubjectStatistics>> departmentStats = allStats.stream()
                .collect(Collectors.groupingBy(SubjectStatistics::getDepartment));

        return departmentStats.entrySet().stream()
                .map(entry -> {
                    String dept = entry.getKey();
                    List<SubjectStatistics> stats = entry.getValue();
                    
                    Double avgPassRate = stats.stream()
                            .mapToDouble(SubjectStatistics::getPassRatePercentage)
                            .average()
                            .orElse(0.0);
                    
                    Double avgGrade = stats.stream()
                            .mapToDouble(SubjectStatistics::getAverageGrade)
                            .average()
                            .orElse(0.0);
                    
                    Integer totalStudents = stats.stream()
                            .mapToInt(SubjectStatistics::getTotalEnrolledStudents)
                            .sum();
                    
                    return new DepartmentComparison(dept, avgPassRate, avgGrade, totalStudents, stats.size());
                })
                .sorted(Comparator.comparing(DepartmentComparison::getAveragePassRate).reversed())
                .collect(Collectors.toList());
    }

    /**
     * Analiza po godinama studija
     */
    public List<SubjectStatistics> analyzeByYearOfStudy(String department, Integer yearOfStudy) {
        return subjectStatisticsRepository.findSubjectsByDepartmentAndYearOfStudy(department, yearOfStudy);
    }

    /**
     * Semestralne analize
     */
    public SemesterAnalysis analyzeSemester(String department, Integer semester, String academicYear) {
        List<SubjectStatistics> semesterStats = subjectStatisticsRepository.findSubjectsBySemester(department, semester)
                .stream()
                .filter(s -> s.getAcademicYear().equals(academicYear))
                .collect(Collectors.toList());

        if (semesterStats.isEmpty()) {
            return new SemesterAnalysis(department, semester, academicYear, 0.0, 0.0, 0, 0);
        }

        Double avgPassRate = semesterStats.stream()
                .mapToDouble(SubjectStatistics::getPassRatePercentage)
                .average()
                .orElse(0.0);

        Double avgGrade = semesterStats.stream()
                .mapToDouble(SubjectStatistics::getAverageGrade)
                .average()
                .orElse(0.0);

        Integer totalStudents = semesterStats.stream()
                .mapToInt(SubjectStatistics::getTotalEnrolledStudents)
                .sum();

        return new SemesterAnalysis(department, semester, academicYear, avgPassRate, avgGrade, totalStudents, semesterStats.size());
    }

    // === HELPER KLASE ===

    public static class DepartmentComprehensiveAnalysis {
        private String department;
        private String academicYear;
        private Double averagePassRate;
        private Double averageGrade;
        private Integer totalStudents;
        private Integer totalSubjects;
        private String hardestSubjectId;

        public DepartmentComprehensiveAnalysis(String department, String academicYear, Double averagePassRate,
                                             Double averageGrade, Integer totalStudents, Integer totalSubjects, String hardestSubjectId) {
            this.department = department;
            this.academicYear = academicYear;
            this.averagePassRate = averagePassRate;
            this.averageGrade = averageGrade;
            this.totalStudents = totalStudents;
            this.totalSubjects = totalSubjects;
            this.hardestSubjectId = hardestSubjectId;
        }

        // Getters
        public String getDepartment() { return department; }
        public String getAcademicYear() { return academicYear; }
        public Double getAveragePassRate() { return averagePassRate; }
        public Double getAverageGrade() { return averageGrade; }
        public Integer getTotalStudents() { return totalStudents; }
        public Integer getTotalSubjects() { return totalSubjects; }
        public String getHardestSubjectId() { return hardestSubjectId; }
    }

    public static class SubjectTrendAnalysis {
        private String subjectId;
        private String trendDirection;
        private Double passRateChange;
        private Double gradeChange;

        public SubjectTrendAnalysis(String subjectId, String trendDirection, Double passRateChange, Double gradeChange) {
            this.subjectId = subjectId;
            this.trendDirection = trendDirection;
            this.passRateChange = passRateChange;
            this.gradeChange = gradeChange;
        }

        // Getters
        public String getSubjectId() { return subjectId; }
        public String getTrendDirection() { return trendDirection; }
        public Double getPassRateChange() { return passRateChange; }
        public Double getGradeChange() { return gradeChange; }
    }

    public static class DepartmentComparison {
        private String department;
        private Double averagePassRate;
        private Double averageGrade;
        private Integer totalStudents;
        private Integer totalSubjects;

        public DepartmentComparison(String department, Double averagePassRate, Double averageGrade, 
                                  Integer totalStudents, Integer totalSubjects) {
            this.department = department;
            this.averagePassRate = averagePassRate;
            this.averageGrade = averageGrade;
            this.totalStudents = totalStudents;
            this.totalSubjects = totalSubjects;
        }

        // Getters
        public String getDepartment() { return department; }
        public Double getAveragePassRate() { return averagePassRate; }
        public Double getAverageGrade() { return averageGrade; }
        public Integer getTotalStudents() { return totalStudents; }
        public Integer getTotalSubjects() { return totalSubjects; }
    }

    public static class SemesterAnalysis {
        private String department;
        private Integer semester;
        private String academicYear;
        private Double averagePassRate;
        private Double averageGrade;
        private Integer totalStudents;
        private Integer totalSubjects;

        public SemesterAnalysis(String department, Integer semester, String academicYear, 
                              Double averagePassRate, Double averageGrade, Integer totalStudents, Integer totalSubjects) {
            this.department = department;
            this.semester = semester;
            this.academicYear = academicYear;
            this.averagePassRate = averagePassRate;
            this.averageGrade = averageGrade;
            this.totalStudents = totalStudents;
            this.totalSubjects = totalSubjects;
        }

        // Getters
        public String getDepartment() { return department; }
        public Integer getSemester() { return semester; }
        public String getAcademicYear() { return academicYear; }
        public Double getAveragePassRate() { return averagePassRate; }
        public Double getAverageGrade() { return averageGrade; }
        public Integer getTotalStudents() { return totalStudents; }
        public Integer getTotalSubjects() { return totalSubjects; }
    }
}