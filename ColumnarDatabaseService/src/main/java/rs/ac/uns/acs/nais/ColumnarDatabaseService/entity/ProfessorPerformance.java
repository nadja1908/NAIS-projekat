package rs.ac.uns.acs.nais.ColumnarDatabaseService.entity;

import org.springframework.data.cassandra.core.cql.Ordering;
import org.springframework.data.cassandra.core.cql.PrimaryKeyType;
import org.springframework.data.cassandra.core.mapping.Column;
import org.springframework.data.cassandra.core.mapping.PrimaryKeyColumn;
import org.springframework.data.cassandra.core.mapping.Table;

@Table("professor_performance")
public class ProfessorPerformance {

    @PrimaryKeyColumn(name = "professor_id", type = PrimaryKeyType.PARTITIONED)
    private Long professorId;

    @PrimaryKeyColumn(name = "academic_year", ordinal = 0, ordering = Ordering.DESCENDING)
    private String academicYear;

    @PrimaryKeyColumn(name = "department", ordinal = 1, ordering = Ordering.ASCENDING)
    private String department;

    @Column("professor_name")
    private String professorName;

    @Column("total_subjects_taught")
    private Integer totalSubjectsTaught;

    @Column("total_students_taught")
    private Integer totalStudentsTaught;

    @Column("total_students_passed")
    private Integer totalStudentsPassed;

    @Column("total_students_failed")
    private Integer totalStudentsFailed;

    @Column("overall_pass_rate")
    private Double overallPassRate;

    @Column("average_grade_given")
    private Double averageGradeGiven;

    @Column("total_exams_conducted")
    private Integer totalExamsConducted;

    @Column("hardest_subject_id")
    private String hardestSubjectId; // Predmet sa najnižom prolaznosću

    @Column("hardest_subject_name")
    private String hardestSubjectName;

    @Column("hardest_subject_pass_rate")
    private Double hardestSubjectPassRate;

    @Column("easiest_subject_id")
    private String easiestSubjectId; // Predmet sa najvišom prolaznosću

    @Column("easiest_subject_name")
    private String easiestSubjectName;

    @Column("easiest_subject_pass_rate")
    private Double easiestSubjectPassRate;

    @Column("performance_consistency_score")
    private Double performanceConsistencyScore; // 0.0-1.0 score of consistency

    // Constructors
    public ProfessorPerformance() {}

    // Getters and Setters
    public Long getProfessorId() {
        return professorId;
    }

    public void setProfessorId(Long professorId) {
        this.professorId = professorId;
    }

    public String getAcademicYear() {
        return academicYear;
    }

    public void setAcademicYear(String academicYear) {
        this.academicYear = academicYear;
    }

    public String getDepartment() {
        return department;
    }

    public void setDepartment(String department) {
        this.department = department;
    }

    public String getProfessorName() {
        return professorName;
    }

    public void setProfessorName(String professorName) {
        this.professorName = professorName;
    }

    public Integer getTotalSubjectsTaught() {
        return totalSubjectsTaught;
    }

    public void setTotalSubjectsTaught(Integer totalSubjectsTaught) {
        this.totalSubjectsTaught = totalSubjectsTaught;
    }

    public Integer getTotalStudentsTaught() {
        return totalStudentsTaught;
    }

    public void setTotalStudentsTaught(Integer totalStudentsTaught) {
        this.totalStudentsTaught = totalStudentsTaught;
    }

    public Integer getTotalStudentsPassed() {
        return totalStudentsPassed;
    }

    public void setTotalStudentsPassed(Integer totalStudentsPassed) {
        this.totalStudentsPassed = totalStudentsPassed;
    }

    public Integer getTotalStudentsFailed() {
        return totalStudentsFailed;
    }

    public void setTotalStudentsFailed(Integer totalStudentsFailed) {
        this.totalStudentsFailed = totalStudentsFailed;
    }

    public Double getOverallPassRate() {
        return overallPassRate;
    }

    public void setOverallPassRate(Double overallPassRate) {
        this.overallPassRate = overallPassRate;
    }

    public Double getAverageGradeGiven() {
        return averageGradeGiven;
    }

    public void setAverageGradeGiven(Double averageGradeGiven) {
        this.averageGradeGiven = averageGradeGiven;
    }

    public Integer getTotalExamsConducted() {
        return totalExamsConducted;
    }

    public void setTotalExamsConducted(Integer totalExamsConducted) {
        this.totalExamsConducted = totalExamsConducted;
    }

    public String getHardestSubjectId() {
        return hardestSubjectId;
    }

    public void setHardestSubjectId(String hardestSubjectId) {
        this.hardestSubjectId = hardestSubjectId;
    }

    public String getHardestSubjectName() {
        return hardestSubjectName;
    }

    public void setHardestSubjectName(String hardestSubjectName) {
        this.hardestSubjectName = hardestSubjectName;
    }

    public Double getHardestSubjectPassRate() {
        return hardestSubjectPassRate;
    }

    public void setHardestSubjectPassRate(Double hardestSubjectPassRate) {
        this.hardestSubjectPassRate = hardestSubjectPassRate;
    }

    public String getEasiestSubjectId() {
        return easiestSubjectId;
    }

    public void setEasiestSubjectId(String easiestSubjectId) {
        this.easiestSubjectId = easiestSubjectId;
    }

    public String getEasiestSubjectName() {
        return easiestSubjectName;
    }

    public void setEasiestSubjectName(String easiestSubjectName) {
        this.easiestSubjectName = easiestSubjectName;
    }

    public Double getEasiestSubjectPassRate() {
        return easiestSubjectPassRate;
    }

    public void setEasiestSubjectPassRate(Double easiestSubjectPassRate) {
        this.easiestSubjectPassRate = easiestSubjectPassRate;
    }

    public Double getPerformanceConsistencyScore() {
        return performanceConsistencyScore;
    }

    public void setPerformanceConsistencyScore(Double performanceConsistencyScore) {
        this.performanceConsistencyScore = performanceConsistencyScore;
    }
}