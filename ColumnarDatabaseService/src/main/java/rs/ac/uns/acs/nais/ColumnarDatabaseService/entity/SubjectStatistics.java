package rs.ac.uns.acs.nais.ColumnarDatabaseService.entity;

import org.springframework.data.cassandra.core.cql.Ordering;
import org.springframework.data.cassandra.core.cql.PrimaryKeyType;
import org.springframework.data.cassandra.core.mapping.Column;
import org.springframework.data.cassandra.core.mapping.PrimaryKeyColumn;
import org.springframework.data.cassandra.core.mapping.Table;

@Table("subject_statistics")
public class SubjectStatistics {

    @PrimaryKeyColumn(name = "department", type = PrimaryKeyType.PARTITIONED)
    private String department;

    @PrimaryKeyColumn(name = "academic_year", ordinal = 0, ordering = Ordering.DESCENDING)
    private String academicYear;

    @PrimaryKeyColumn(name = "subject_id", ordinal = 1, ordering = Ordering.ASCENDING)
    private String subjectId;

    @Column("subject_name")
    private String subjectName;

    @Column("subject_code")
    private String subjectCode;

    @Column("professor_id")
    private Long professorId;

    @Column("professor_name")
    private String professorName;

    @Column("total_enrolled_students")
    private Integer totalEnrolledStudents;

    @Column("total_passed_students")
    private Integer totalPassedStudents;

    @Column("total_failed_students")
    private Integer totalFailedStudents;

    @Column("pass_rate_percentage")
    private Double passRatePercentage;

    @Column("average_grade")
    private Double averageGrade;

    @Column("total_attempts")
    private Integer totalAttempts;

    @Column("grade_6_count")
    private Integer grade6Count;

    @Column("grade_7_count")
    private Integer grade7Count;

    @Column("grade_8_count")
    private Integer grade8Count;

    @Column("grade_9_count")
    private Integer grade9Count;

    @Column("grade_10_count")
    private Integer grade10Count;

    @Column("year_of_study")
    private Integer yearOfStudy;

    @Column("semester")
    private Integer semester;

    @Column("ects_points")
    private Integer ectsPoints;

    // Constructors
    public SubjectStatistics() {}

    // Getters and Setters
    public String getDepartment() {
        return department;
    }

    public void setDepartment(String department) {
        this.department = department;
    }

    public String getAcademicYear() {
        return academicYear;
    }

    public void setAcademicYear(String academicYear) {
        this.academicYear = academicYear;
    }

    public String getSubjectId() {
        return subjectId;
    }

    public void setSubjectId(String subjectId) {
        this.subjectId = subjectId;
    }

    public String getSubjectName() {
        return subjectName;
    }

    public void setSubjectName(String subjectName) {
        this.subjectName = subjectName;
    }

    public String getSubjectCode() {
        return subjectCode;
    }

    public void setSubjectCode(String subjectCode) {
        this.subjectCode = subjectCode;
    }

    public Long getProfessorId() {
        return professorId;
    }

    public void setProfessorId(Long professorId) {
        this.professorId = professorId;
    }

    public String getProfessorName() {
        return professorName;
    }

    public void setProfessorName(String professorName) {
        this.professorName = professorName;
    }

    public Integer getTotalEnrolledStudents() {
        return totalEnrolledStudents;
    }

    public void setTotalEnrolledStudents(Integer totalEnrolledStudents) {
        this.totalEnrolledStudents = totalEnrolledStudents;
    }

    public Integer getTotalPassedStudents() {
        return totalPassedStudents;
    }

    public void setTotalPassedStudents(Integer totalPassedStudents) {
        this.totalPassedStudents = totalPassedStudents;
    }

    public Integer getTotalFailedStudents() {
        return totalFailedStudents;
    }

    public void setTotalFailedStudents(Integer totalFailedStudents) {
        this.totalFailedStudents = totalFailedStudents;
    }

    public Double getPassRatePercentage() {
        return passRatePercentage;
    }

    public void setPassRatePercentage(Double passRatePercentage) {
        this.passRatePercentage = passRatePercentage;
    }

    public Double getAverageGrade() {
        return averageGrade;
    }

    public void setAverageGrade(Double averageGrade) {
        this.averageGrade = averageGrade;
    }

    public Integer getTotalAttempts() {
        return totalAttempts;
    }

    public void setTotalAttempts(Integer totalAttempts) {
        this.totalAttempts = totalAttempts;
    }

    public Integer getGrade6Count() {
        return grade6Count;
    }

    public void setGrade6Count(Integer grade6Count) {
        this.grade6Count = grade6Count;
    }

    public Integer getGrade7Count() {
        return grade7Count;
    }

    public void setGrade7Count(Integer grade7Count) {
        this.grade7Count = grade7Count;
    }

    public Integer getGrade8Count() {
        return grade8Count;
    }

    public void setGrade8Count(Integer grade8Count) {
        this.grade8Count = grade8Count;
    }

    public Integer getGrade9Count() {
        return grade9Count;
    }

    public void setGrade9Count(Integer grade9Count) {
        this.grade9Count = grade9Count;
    }

    public Integer getGrade10Count() {
        return grade10Count;
    }

    public void setGrade10Count(Integer grade10Count) {
        this.grade10Count = grade10Count;
    }

    public Integer getYearOfStudy() {
        return yearOfStudy;
    }

    public void setYearOfStudy(Integer yearOfStudy) {
        this.yearOfStudy = yearOfStudy;
    }

    public Integer getSemester() {
        return semester;
    }

    public void setSemester(Integer semester) {
        this.semester = semester;
    }

    public Integer getEctsPoints() {
        return ectsPoints;
    }

    public void setEctsPoints(Integer ectsPoints) {
        this.ectsPoints = ectsPoints;
    }
}