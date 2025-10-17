package rs.ac.uns.acs.nais.ColumnarDatabaseService.dto;

import java.time.LocalDate;

/**
 * DTO za ocenu studenta
 */
public class StudentGradeDTO {
    private Long studentId;
    private String subjectId;
    private String academicYear;
    private LocalDate examDate;
    private Double grade;
    private String examType;
    private Long professorId;
    private String department;

    // Konstruktori
    public StudentGradeDTO() {}

    public StudentGradeDTO(Long studentId, String subjectId, String academicYear, LocalDate examDate,
                          Double grade, String examType, Long professorId, String department) {
        this.studentId = studentId;
        this.subjectId = subjectId;
        this.academicYear = academicYear;
        this.examDate = examDate;
        this.grade = grade;
        this.examType = examType;
        this.professorId = professorId;
        this.department = department;
    }

    // Getters i Setters
    public Long getStudentId() { return studentId; }
    public void setStudentId(Long studentId) { this.studentId = studentId; }

    public String getSubjectId() { return subjectId; }
    public void setSubjectId(String subjectId) { this.subjectId = subjectId; }

    public String getAcademicYear() { return academicYear; }
    public void setAcademicYear(String academicYear) { this.academicYear = academicYear; }

    public LocalDate getExamDate() { return examDate; }
    public void setExamDate(LocalDate examDate) { this.examDate = examDate; }

    public Double getGrade() { return grade; }
    public void setGrade(Double grade) { this.grade = grade; }

    public String getExamType() { return examType; }
    public void setExamType(String examType) { this.examType = examType; }

    public Long getProfessorId() { return professorId; }
    public void setProfessorId(Long professorId) { this.professorId = professorId; }

    public String getDepartment() { return department; }
    public void setDepartment(String department) { this.department = department; }
}