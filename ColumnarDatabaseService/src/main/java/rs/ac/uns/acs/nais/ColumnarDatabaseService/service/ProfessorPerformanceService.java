package rs.ac.uns.acs.nais.ColumnarDatabaseService.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import rs.ac.uns.acs.nais.ColumnarDatabaseService.entity.ProfessorPerformance;
import rs.ac.uns.acs.nais.ColumnarDatabaseService.repository.ProfessorPerformanceRepository;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class ProfessorPerformanceService {

    @Autowired
    private ProfessorPerformanceRepository professorPerformanceRepository;

    // === OSNOVNI CRUD ===
    
    public ProfessorPerformance savePerformance(ProfessorPerformance performance) {
        return professorPerformanceRepository.save(performance);
    }

    public List<ProfessorPerformance> getAllPerformances() {
        return professorPerformanceRepository.findAll();
    }

    public List<ProfessorPerformance> getPerformancesByProfessor(Long professorId) {
        return professorPerformanceRepository.findByProfessorId(professorId);
    }

    // === EVALUACIJA I RANKING ===

    /**
     * Top 10 profesora po prolaznosti studenata
     */
    public List<ProfessorRanking> getTopProfessorsByPassRate(String academicYear, int limit) {
        return professorPerformanceRepository.findAllPerformancesByYear(academicYear)
                .stream()
                .sorted(Comparator.comparing(ProfessorPerformance::getOverallPassRate).reversed())
                .limit(limit)
                .map(p -> new ProfessorRanking(p.getProfessorId(), p.getOverallPassRate(), 
                                             p.getAverageGradeGiven(), p.getTotalStudentsTaught(), "TOP_PERFORMER"))
                .collect(Collectors.toList());
    }

    /**
     * Profesori koji trebaju podršku (niske prolaznosti)
     */
    public List<ProfessorSupport> getProfessorsNeedingSupport(double passRateThreshold) {
        List<ProfessorPerformance> strugglingProfs = professorPerformanceRepository
                .findProfessorsNeedingSupport(passRateThreshold);

        return strugglingProfs.stream()
                .map(p -> {
                    String supportType = determineSupportType(p);
                    String recommendation = generateRecommendation(p);
                    return new ProfessorSupport(p.getProfessorId(), p.getOverallPassRate(), 
                                              supportType, recommendation, p.getAcademicYear());
                })
                .collect(Collectors.toList());
    }

    /**
     * Kompletna evaluacija profesora
     */
    public ProfessorEvaluation evaluateProfessor(Long professorId, String academicYear) {
        Optional<ProfessorPerformance> perfOpt = professorPerformanceRepository
                .findByProfessorIdAndYear(professorId, academicYear);

        if (perfOpt.isEmpty()) {
            return new ProfessorEvaluation(professorId, academicYear, "NO_DATA", 0.0, "N/A", "N/A");
        }

        ProfessorPerformance perf = perfOpt.get();
        
        // Determine overall rating
        String overallRating = calculateOverallRating(perf);
        double performanceScore = calculatePerformanceScore(perf);
        String strengths = identifyStrengths(perf);
        String improvements = identifyImprovements(perf);

        return new ProfessorEvaluation(professorId, academicYear, overallRating, 
                                     performanceScore, strengths, improvements);
    }

    /**
     * Trend analiza profesora kroz godine
     */
    public ProfessorTrendAnalysis analyzeProfessorTrends(Long professorId) {
        List<ProfessorPerformance> trends = professorPerformanceRepository.findProfessorPerformanceTrends(professorId);
        
        if (trends.size() < 2) {
            return new ProfessorTrendAnalysis(professorId, "INSUFFICIENT_DATA", 0.0, 0.0, "STABLE");
        }

        // Sort by year
        trends.sort(Comparator.comparing(ProfessorPerformance::getAcademicYear));

        ProfessorPerformance oldest = trends.get(0);
        ProfessorPerformance newest = trends.get(trends.size() - 1);

        double passRateChange = newest.getOverallPassRate() - oldest.getOverallPassRate();
        double gradeChange = newest.getAverageGradeGiven() - oldest.getAverageGradeGiven();

        String trendDirection;
        if (passRateChange > 5.0) {
            trendDirection = "IMPROVING";
        } else if (passRateChange < -5.0) {
            trendDirection = "DECLINING";
        } else {
            trendDirection = "STABLE";
        }

        return new ProfessorTrendAnalysis(professorId, trendDirection, passRateChange, gradeChange, 
                                        calculateTrendDescription(passRateChange, gradeChange));
    }

    /**
     * Workload analiza - preopterećeni profesori
     */
    public List<ProfessorWorkload> analyzeWorkload(String academicYear, int studentThreshold, int subjectThreshold) {
        List<ProfessorPerformance> allPerformances = professorPerformanceRepository.findAllPerformancesByYear(academicYear);

        return allPerformances.stream()
                .filter(p -> p.getTotalStudentsTaught() > studentThreshold || p.getTotalSubjectsTaught() > subjectThreshold)
                .map(p -> {
                    String workloadLevel = determineWorkloadLevel(p, studentThreshold, subjectThreshold);
                    double efficiency = calculateEfficiency(p);
                    return new ProfessorWorkload(p.getProfessorId(), p.getTotalStudentsTaught(), 
                                               p.getTotalSubjectsTaught(), workloadLevel, efficiency);
                })
                .sorted(Comparator.comparing(ProfessorWorkload::getWorkloadEfficiency).reversed())
                .collect(Collectors.toList());
    }

    /**
     * Consistency analiza - profesori sa konzistentnim performansama
     */
    public List<ProfessorConsistency> analyzeConsistency(double consistencyThreshold) {
        return professorPerformanceRepository.findConsistentProfessors(consistencyThreshold)
                .stream()
                .map(p -> new ProfessorConsistency(p.getProfessorId(), p.getPerformanceConsistencyScore(), 
                                                 p.getOverallPassRate(), "CONSISTENT", p.getAcademicYear()))
                .collect(Collectors.toList());
    }

    /**
     * Subject difficulty analysis
     */
    public List<ProfessorSubjectDifficulty> analyzeSubjectDifficulty(String academicYear) {
        return professorPerformanceRepository.findAllPerformancesByYear(academicYear)
                .stream()
                .map(p -> {
                    String difficultyAssessment = assessDifficulty(p);
                    return new ProfessorSubjectDifficulty(p.getProfessorId(), 
                                                        p.getHardestSubjectId(), p.getHardestSubjectPassRate(),
                                                        p.getEasiestSubjectId(), p.getEasiestSubjectPassRate(),
                                                        difficultyAssessment);
                })
                .collect(Collectors.toList());
    }

    // === HELPER METHODS ===

    private String determineSupportType(ProfessorPerformance p) {
        if (p.getOverallPassRate() < 30.0) {
            return "URGENT_INTERVENTION";
        } else if (p.getOverallPassRate() < 50.0) {
            return "TEACHING_SUPPORT";
        } else if (p.getPerformanceConsistencyScore() < 0.5) {
            return "CONSISTENCY_COACHING";
        } else {
            return "MINOR_ADJUSTMENT";
        }
    }

    private String generateRecommendation(ProfessorPerformance p) {
        if (p.getOverallPassRate() < 40.0) {
            return "Review teaching methods, consider peer mentoring, adjust assessment criteria";
        } else if (p.getTotalStudentsTaught() > 200) {
            return "Reduce course load, implement teaching assistants, optimize class sizes";
        } else {
            return "Focus on student engagement techniques, regular feedback sessions";
        }
    }

    private String calculateOverallRating(ProfessorPerformance p) {
        double score = calculatePerformanceScore(p);
        if (score >= 85.0) return "EXCELLENT";
        else if (score >= 70.0) return "GOOD";
        else if (score >= 55.0) return "SATISFACTORY";
        else return "NEEDS_IMPROVEMENT";
    }

    private double calculatePerformanceScore(ProfessorPerformance p) {
        // Weighted score: 50% pass rate, 30% consistency, 20% grade balance
        return (p.getOverallPassRate() * 0.5) + 
               (p.getPerformanceConsistencyScore() * 30.0) + 
               (Math.min(p.getAverageGradeGiven() / 10.0 * 100, 20.0));
    }

    private String identifyStrengths(ProfessorPerformance p) {
        StringBuilder strengths = new StringBuilder();
        if (p.getOverallPassRate() > 80.0) strengths.append("High pass rates, ");
        if (p.getPerformanceConsistencyScore() > 0.8) strengths.append("Consistent performance, ");
        if (p.getTotalSubjectsTaught() > 3) strengths.append("Versatile teaching, ");
        
        return strengths.length() > 0 ? strengths.substring(0, strengths.length() - 2) : "Dedicated educator";
    }

    private String identifyImprovements(ProfessorPerformance p) {
        StringBuilder improvements = new StringBuilder();
        if (p.getOverallPassRate() < 60.0) improvements.append("Improve pass rates, ");
        if (p.getPerformanceConsistencyScore() < 0.6) improvements.append("Increase consistency, ");
        if (p.getHardestSubjectPassRate() < 40.0) improvements.append("Address difficult subjects, ");
        
        return improvements.length() > 0 ? improvements.substring(0, improvements.length() - 2) : "Continue excellence";
    }

    private String calculateTrendDescription(double passRateChange, double gradeChange) {
        if (passRateChange > 10.0) return "Significant improvement in student success";
        else if (passRateChange < -10.0) return "Concerning decline in performance";
        else if (Math.abs(gradeChange) > 1.0) return "Notable changes in grading patterns";
        else return "Stable performance with minor fluctuations";
    }

    private String determineWorkloadLevel(ProfessorPerformance p, int studentThreshold, int subjectThreshold) {
        if (p.getTotalStudentsTaught() > studentThreshold * 1.5 || p.getTotalSubjectsTaught() > subjectThreshold * 1.5) {
            return "OVERLOADED";
        } else if (p.getTotalStudentsTaught() > studentThreshold || p.getTotalSubjectsTaught() > subjectThreshold) {
            return "HIGH_LOAD";
        } else {
            return "NORMAL_LOAD";
        }
    }

    private double calculateEfficiency(ProfessorPerformance p) {
        // Efficiency = pass rate per student taught (normalized)
        return (p.getOverallPassRate() / 100.0) * (100.0 / Math.max(p.getTotalStudentsTaught(), 1));
    }

    private String assessDifficulty(ProfessorPerformance p) {
        double difficultyGap = p.getEasiestSubjectPassRate() - p.getHardestSubjectPassRate();
        if (difficultyGap > 40.0) return "HIGH_VARIABILITY";
        else if (difficultyGap > 20.0) return "MODERATE_VARIABILITY";
        else return "CONSISTENT_DIFFICULTY";
    }

    // === HELPER KLASE ===

    public static class ProfessorRanking {
        private Long professorId;
        private Double passRate;
        private Double averageGrade;
        private Integer totalStudents;
        private String category;

        public ProfessorRanking(Long professorId, Double passRate, Double averageGrade, 
                              Integer totalStudents, String category) {
            this.professorId = professorId;
            this.passRate = passRate;
            this.averageGrade = averageGrade;
            this.totalStudents = totalStudents;
            this.category = category;
        }

        // Getters
        public Long getProfessorId() { return professorId; }
        public Double getPassRate() { return passRate; }
        public Double getAverageGrade() { return averageGrade; }
        public Integer getTotalStudents() { return totalStudents; }
        public String getCategory() { return category; }
    }

    public static class ProfessorSupport {
        private Long professorId;
        private Double currentPassRate;
        private String supportType;
        private String recommendation;
        private String academicYear;

        public ProfessorSupport(Long professorId, Double currentPassRate, String supportType, 
                              String recommendation, String academicYear) {
            this.professorId = professorId;
            this.currentPassRate = currentPassRate;
            this.supportType = supportType;
            this.recommendation = recommendation;
            this.academicYear = academicYear;
        }

        // Getters
        public Long getProfessorId() { return professorId; }
        public Double getCurrentPassRate() { return currentPassRate; }
        public String getSupportType() { return supportType; }
        public String getRecommendation() { return recommendation; }
        public String getAcademicYear() { return academicYear; }
    }

    public static class ProfessorEvaluation {
        private Long professorId;
        private String academicYear;
        private String overallRating;
        private Double performanceScore;
        private String strengths;
        private String improvements;

        public ProfessorEvaluation(Long professorId, String academicYear, String overallRating, 
                                 Double performanceScore, String strengths, String improvements) {
            this.professorId = professorId;
            this.academicYear = academicYear;
            this.overallRating = overallRating;
            this.performanceScore = performanceScore;
            this.strengths = strengths;
            this.improvements = improvements;
        }

        // Getters
        public Long getProfessorId() { return professorId; }
        public String getAcademicYear() { return academicYear; }
        public String getOverallRating() { return overallRating; }
        public Double getPerformanceScore() { return performanceScore; }
        public String getStrengths() { return strengths; }
        public String getImprovements() { return improvements; }
    }

    public static class ProfessorTrendAnalysis {
        private Long professorId;
        private String trendDirection;
        private Double passRateChange;
        private Double gradeChange;
        private String description;

        public ProfessorTrendAnalysis(Long professorId, String trendDirection, Double passRateChange, 
                                    Double gradeChange, String description) {
            this.professorId = professorId;
            this.trendDirection = trendDirection;
            this.passRateChange = passRateChange;
            this.gradeChange = gradeChange;
            this.description = description;
        }

        // Getters
        public Long getProfessorId() { return professorId; }
        public String getTrendDirection() { return trendDirection; }
        public Double getPassRateChange() { return passRateChange; }
        public Double getGradeChange() { return gradeChange; }
        public String getDescription() { return description; }
    }

    public static class ProfessorWorkload {
        private Long professorId;
        private Integer totalStudents;
        private Integer totalSubjects;
        private String workloadLevel;
        private Double workloadEfficiency;

        public ProfessorWorkload(Long professorId, Integer totalStudents, Integer totalSubjects, 
                               String workloadLevel, Double workloadEfficiency) {
            this.professorId = professorId;
            this.totalStudents = totalStudents;
            this.totalSubjects = totalSubjects;
            this.workloadLevel = workloadLevel;
            this.workloadEfficiency = workloadEfficiency;
        }

        // Getters
        public Long getProfessorId() { return professorId; }
        public Integer getTotalStudents() { return totalStudents; }
        public Integer getTotalSubjects() { return totalSubjects; }
        public String getWorkloadLevel() { return workloadLevel; }
        public Double getWorkloadEfficiency() { return workloadEfficiency; }
    }

    public static class ProfessorConsistency {
        private Long professorId;
        private Double consistencyScore;
        private Double passRate;
        private String consistencyLevel;
        private String academicYear;

        public ProfessorConsistency(Long professorId, Double consistencyScore, Double passRate, 
                                  String consistencyLevel, String academicYear) {
            this.professorId = professorId;
            this.consistencyScore = consistencyScore;
            this.passRate = passRate;
            this.consistencyLevel = consistencyLevel;
            this.academicYear = academicYear;
        }

        // Getters
        public Long getProfessorId() { return professorId; }
        public Double getConsistencyScore() { return consistencyScore; }
        public Double getPassRate() { return passRate; }
        public String getConsistencyLevel() { return consistencyLevel; }
        public String getAcademicYear() { return academicYear; }
    }

    public static class ProfessorSubjectDifficulty {
        private Long professorId;
        private String hardestSubjectId;
        private Double hardestSubjectPassRate;
        private String easiestSubjectId;
        private Double easiestSubjectPassRate;
        private String difficultyAssessment;

        public ProfessorSubjectDifficulty(Long professorId, String hardestSubjectId, Double hardestSubjectPassRate,
                                        String easiestSubjectId, Double easiestSubjectPassRate, String difficultyAssessment) {
            this.professorId = professorId;
            this.hardestSubjectId = hardestSubjectId;
            this.hardestSubjectPassRate = hardestSubjectPassRate;
            this.easiestSubjectId = easiestSubjectId;
            this.easiestSubjectPassRate = easiestSubjectPassRate;
            this.difficultyAssessment = difficultyAssessment;
        }

        // Getters
        public Long getProfessorId() { return professorId; }
        public String getHardestSubjectId() { return hardestSubjectId; }
        public Double getHardestSubjectPassRate() { return hardestSubjectPassRate; }
        public String getEasiestSubjectId() { return easiestSubjectId; }
        public Double getEasiestSubjectPassRate() { return easiestSubjectPassRate; }
        public String getDifficultyAssessment() { return difficultyAssessment; }
    }
}