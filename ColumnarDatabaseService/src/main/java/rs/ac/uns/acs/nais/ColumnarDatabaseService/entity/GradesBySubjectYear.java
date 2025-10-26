package rs.ac.uns.acs.nais.ColumnarDatabaseService.entity;

import org.springframework.data.cassandra.core.cql.Ordering;
import org.springframework.data.cassandra.core.cql.PrimaryKeyType;
import org.springframework.data.cassandra.core.mapping.Column;
import org.springframework.data.cassandra.core.mapping.PrimaryKeyColumn;
import org.springframework.data.cassandra.core.mapping.Table;

import java.time.LocalDateTime;

@Table("grades_by_subject_year")
public class GradesBySubjectYear {

    @PrimaryKeyColumn(name = "subject_id", type = PrimaryKeyType.PARTITIONED)
    private String subjectId;

    @PrimaryKeyColumn(name = "academic_year", ordinal = 0)
    private String academicYear;

    @PrimaryKeyColumn(name = "exam_date", ordinal = 1, ordering = Ordering.DESCENDING)
    private LocalDateTime examDate;

    @PrimaryKeyColumn(name = "student_id", ordinal = 2)
    private Long studentId;

    @Column("grade")
    private Double grade;

    @Column("exam_type")
    private String examType;

    @Column("passed")
    private Boolean passed;

    @Column("professor_id")
    private Long professorId;

    @Column("department")
    private String department;

    public GradesBySubjectYear() {}

    // Getters and setters
    public String getSubjectId() { return subjectId; }
    public void setSubjectId(String subjectId) { this.subjectId = subjectId; }
    public String getAcademicYear() { return academicYear; }
    public void setAcademicYear(String academicYear) { this.academicYear = academicYear; }
    public LocalDateTime getExamDate() { return examDate; }
    public void setExamDate(LocalDateTime examDate) { this.examDate = examDate; }
    public Long getStudentId() { return studentId; }
    public void setStudentId(Long studentId) { this.studentId = studentId; }
    public Double getGrade() { return grade; }
    public void setGrade(Double grade) { this.grade = grade; }
    public String getExamType() { return examType; }
    public void setExamType(String examType) { this.examType = examType; }
    public Boolean getPassed() { return passed; }
    public void setPassed(Boolean passed) { this.passed = passed; }
    public Long getProfessorId() { return professorId; }
    public void setProfessorId(Long professorId) { this.professorId = professorId; }
    public String getDepartment() { return department; }
    public void setDepartment(String department) { this.department = department; }
}
