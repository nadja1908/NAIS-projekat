package rs.ac.uns.acs.nais.ColumnarDatabaseService.entity;

import org.springframework.data.cassandra.core.cql.Ordering;
import org.springframework.data.cassandra.core.cql.PrimaryKeyType;
import org.springframework.data.cassandra.core.mapping.Column;
import org.springframework.data.cassandra.core.mapping.PrimaryKeyColumn;
import org.springframework.data.cassandra.core.mapping.Table;

import java.time.LocalDateTime;

@Table("student_grades")
public class StudentGrade {

    @PrimaryKeyColumn(name = "student_id", type = PrimaryKeyType.PARTITIONED)
    private Long studentId;

    @PrimaryKeyColumn(name = "academic_year", ordinal = 0, ordering = Ordering.DESCENDING)
    private String academicYear; // e.g., "2024/2025"

    @PrimaryKeyColumn(name = "subject_id", ordinal = 1, ordering = Ordering.ASCENDING)
    private String subjectId;

    @PrimaryKeyColumn(name = "exam_date", ordinal = 2, ordering = Ordering.DESCENDING)
    private LocalDateTime examDate;

    @Column("grade")
    private Double grade; // 5.0-10.0 or null if failed

    @Column("exam_type")
    private String examType; // "REDOVAN", "POPRAVNI", "SPECIJALNI"

    @Column("passed")
    private Boolean passed;

    @Column("attempt_number")
    private Integer attemptNumber; // Prvi, drugi, treći pokušaj

    @Column("subject_name")
    private String subjectName;


    @Column("department")
    private String department;

    @Column("professor_id")
    private Long professorId;

    @Column("professor_name")
    private String professorName;

    @Column("ects_points")
    private Integer ectsPoints;

    @Column("semester")
    private Integer semester;

    @Column("year_of_study")
    private Integer yearOfStudy;

    // Constructors
    public StudentGrade() {}

    // Getters and Setters
    public Long getStudentId() {
        return studentId;
    }

    public void setStudentId(Long studentId) {
        this.studentId = studentId;
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

    public LocalDateTime getExamDate() {
        return examDate;
    }

    public void setExamDate(LocalDateTime examDate) {
        this.examDate = examDate;
    }

    public Double getGrade() {
        return grade;
    }

    public void setGrade(Double grade) {
        this.grade = grade;
    }

    public String getExamType() {
        return examType;
    }

    public void setExamType(String examType) {
        this.examType = examType;
    }

    public Boolean getPassed() {
        return passed;
    }

    public void setPassed(Boolean passed) {
        this.passed = passed;
    }

    public Integer getAttemptNumber() {
        return attemptNumber;
    }

    public void setAttemptNumber(Integer attemptNumber) {
        this.attemptNumber = attemptNumber;
    }

    public String getSubjectName() {
        return subjectName;
    }

    public void setSubjectName(String subjectName) {
        this.subjectName = subjectName;
    }


    public String getDepartment() {
        return department;
    }

    public void setDepartment(String department) {
        this.department = department;
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

    public Integer getEctsPoints() {
        return ectsPoints;
    }

    public void setEctsPoints(Integer ectsPoints) {
        this.ectsPoints = ectsPoints;
    }

    public Integer getSemester() {
        return semester;
    }

    public void setSemester(Integer semester) {
        this.semester = semester;
    }

    public Integer getYearOfStudy() {
        return yearOfStudy;
    }

    public void setYearOfStudy(Integer yearOfStudy) {
        this.yearOfStudy = yearOfStudy;
    }
}