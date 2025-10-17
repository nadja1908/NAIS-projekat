package rs.ac.uns.acs.nais.ColumnarDatabaseService.dto;

/**
 * DTO za analitičke response-e sa akademskim performansama
 */
public class AnalyticsResponseDTO {
    
    /**
     * DTO za analizu akademskog uspeha studenta
     */
    public static class StudentAnalysisDTO {
        private Long studentId;
        private Double averageGrade;
        private Integer passedExams;
        private Integer failedExams;
        private Double successRate;
        private String performanceLevel;

        public StudentAnalysisDTO(Long studentId, Double averageGrade, Integer passedExams, 
                                Integer failedExams, Double successRate) {
            this.studentId = studentId;
            this.averageGrade = averageGrade;
            this.passedExams = passedExams;
            this.failedExams = failedExams;
            this.successRate = successRate;
            this.performanceLevel = determinePerformanceLevel(averageGrade, successRate);
        }

        private String determinePerformanceLevel(Double avgGrade, Double successRate) {
            if (avgGrade >= 8.5 && successRate >= 90.0) return "EXCELLENT";
            else if (avgGrade >= 7.5 && successRate >= 80.0) return "VERY_GOOD";
            else if (avgGrade >= 6.5 && successRate >= 70.0) return "GOOD";
            else if (avgGrade >= 6.0 && successRate >= 60.0) return "SATISFACTORY";
            else return "NEEDS_IMPROVEMENT";
        }

        // Getters
        public Long getStudentId() { return studentId; }
        public Double getAverageGrade() { return averageGrade; }
        public Integer getPassedExams() { return passedExams; }
        public Integer getFailedExams() { return failedExams; }
        public Double getSuccessRate() { return successRate; }
        public String getPerformanceLevel() { return performanceLevel; }
    }

    /**
     * DTO za departmentske analize
     */
    public static class DepartmentAnalysisDTO {
        private String department;
        private String academicYear;
        private Double averageGrade;
        private Double averagePassRate;
        private Integer totalStudents;
        private Integer totalSubjects;
        private String performanceRank;

        public DepartmentAnalysisDTO(String department, String academicYear, Double averageGrade,
                                   Double averagePassRate, Integer totalStudents, Integer totalSubjects) {
            this.department = department;
            this.academicYear = academicYear;
            this.averageGrade = averageGrade;
            this.averagePassRate = averagePassRate;
            this.totalStudents = totalStudents;
            this.totalSubjects = totalSubjects;
            this.performanceRank = determineRank(averagePassRate);
        }

        private String determineRank(Double passRate) {
            if (passRate >= 85.0) return "TOP_PERFORMER";
            else if (passRate >= 70.0) return "GOOD_PERFORMER";
            else if (passRate >= 60.0) return "AVERAGE_PERFORMER";
            else return "NEEDS_ATTENTION";
        }

        // Getters
        public String getDepartment() { return department; }
        public String getAcademicYear() { return academicYear; }
        public Double getAverageGrade() { return averageGrade; }
        public Double getAveragePassRate() { return averagePassRate; }
        public Integer getTotalStudents() { return totalStudents; }
        public Integer getTotalSubjects() { return totalSubjects; }
        public String getPerformanceRank() { return performanceRank; }
    }

    /**
     * DTO za evaluaciju profesora
     */
    public static class ProfessorEvaluationDTO {
        private Long professorId;
        private String academicYear;
        private String overallRating;
        private Double performanceScore;
        private Double passRate;
        private Double averageGrade;
        private String strengths;
        private String recommendations;

        public ProfessorEvaluationDTO(Long professorId, String academicYear, String overallRating,
                                    Double performanceScore, Double passRate, Double averageGrade,
                                    String strengths, String recommendations) {
            this.professorId = professorId;
            this.academicYear = academicYear;
            this.overallRating = overallRating;
            this.performanceScore = performanceScore;
            this.passRate = passRate;
            this.averageGrade = averageGrade;
            this.strengths = strengths;
            this.recommendations = recommendations;
        }

        // Getters
        public Long getProfessorId() { return professorId; }
        public String getAcademicYear() { return academicYear; }
        public String getOverallRating() { return overallRating; }
        public Double getPerformanceScore() { return performanceScore; }
        public Double getPassRate() { return passRate; }
        public Double getAverageGrade() { return averageGrade; }
        public String getStrengths() { return strengths; }
        public String getRecommendations() { return recommendations; }
    }

    /**
     * DTO za statistike predmeta
     */
    public static class SubjectStatisticsDTO {
        private String subjectId;
        private String subjectName;
        private String department;
        private String academicYear;
        private Double averageGrade;
        private Double passRate;
        private Integer totalStudents;
        private Integer passedStudents;
        private Integer failedStudents;
        private String difficultyLevel;

        public SubjectStatisticsDTO(String subjectId, String subjectName, String department,
                                  String academicYear, Double averageGrade, Double passRate,
                                  Integer totalStudents, Integer passedStudents, Integer failedStudents) {
            this.subjectId = subjectId;
            this.subjectName = subjectName;
            this.department = department;
            this.academicYear = academicYear;
            this.averageGrade = averageGrade;
            this.passRate = passRate;
            this.totalStudents = totalStudents;
            this.passedStudents = passedStudents;
            this.failedStudents = failedStudents;
            this.difficultyLevel = determineDifficulty(passRate);
        }

        private String determineDifficulty(Double passRate) {
            if (passRate >= 90.0) return "VERY_EASY";
            else if (passRate >= 75.0) return "EASY";
            else if (passRate >= 60.0) return "MODERATE";
            else if (passRate >= 40.0) return "DIFFICULT";
            else return "VERY_DIFFICULT";
        }

        // Getters
        public String getSubjectId() { return subjectId; }
        public String getSubjectName() { return subjectName; }
        public String getDepartment() { return department; }
        public String getAcademicYear() { return academicYear; }
        public Double getAverageGrade() { return averageGrade; }
        public Double getPassRate() { return passRate; }
        public Integer getTotalStudents() { return totalStudents; }
        public Integer getPassedStudents() { return passedStudents; }
        public Integer getFailedStudents() { return failedStudents; }
        public String getDifficultyLevel() { return difficultyLevel; }
    }

    /**
     * DTO za trend analize
     */
    public static class TrendAnalysisDTO {
        private String identifier; // može biti subjectId, professorId, department...
        private String trendDirection; // IMPROVING, DECLINING, STABLE
        private Double changePercentage;
        private String timeperiod;
        private String description;

        public TrendAnalysisDTO(String identifier, String trendDirection, Double changePercentage,
                              String timeperiod, String description) {
            this.identifier = identifier;
            this.trendDirection = trendDirection;
            this.changePercentage = changePercentage;
            this.timeperiod = timeperiod;
            this.description = description;
        }

        // Getters
        public String getIdentifier() { return identifier; }
        public String getTrendDirection() { return trendDirection; }
        public Double getChangePercentage() { return changePercentage; }
        public String getTimeperiod() { return timeperiod; }
        public String getDescription() { return description; }
    }
}