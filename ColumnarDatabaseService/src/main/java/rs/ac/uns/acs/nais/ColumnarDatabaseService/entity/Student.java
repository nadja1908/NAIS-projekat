package rs.ac.uns.acs.nais.ColumnarDatabaseService.entity;

import org.springframework.data.cassandra.core.mapping.Column;
import org.springframework.data.cassandra.core.mapping.PrimaryKey;
import org.springframework.data.cassandra.core.mapping.Table;

@Table("students")
public class Student {

    @PrimaryKey("student_id")
    private Long studentId;

    @Column("first_name")
    private String firstName;

    @Column("last_name")
    private String lastName;

    @Column("index_number")
    private String indexNumber;

    @Column("study_program")
    private String studyProgram;

    @Column("current_year_of_study")
    private Integer currentYearOfStudy;

    @Column("enrollment_year")
    private Integer enrollmentYear;

    @Column("enrollment_date")
    private java.time.LocalDate enrollmentDate;

    @Column("is_active")
    private Boolean isActive;

    // Constructors
    public Student() {}

    public Student(Long studentId, String firstName, String lastName, String indexNumber, 
                   String studyProgram, Integer currentYearOfStudy, Integer enrollmentYear, 
                   java.time.LocalDate enrollmentDate, Boolean isActive) {
        this.studentId = studentId;
        this.firstName = firstName;
        this.lastName = lastName;
        this.indexNumber = indexNumber;
        this.studyProgram = studyProgram;
        this.currentYearOfStudy = currentYearOfStudy;
        this.enrollmentYear = enrollmentYear;
        this.enrollmentDate = enrollmentDate;
        this.isActive = isActive;
    }

    // Getters and Setters
    public Long getStudentId() {
        return studentId;
    }

    public void setStudentId(Long studentId) {
        this.studentId = studentId;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getIndexNumber() {
        return indexNumber;
    }

    public void setIndexNumber(String indexNumber) {
        this.indexNumber = indexNumber;
    }

    public String getStudyProgram() {
        return studyProgram;
    }

    public void setStudyProgram(String studyProgram) {
        this.studyProgram = studyProgram;
    }

    public Integer getCurrentYearOfStudy() {
        return currentYearOfStudy;
    }

    public void setCurrentYearOfStudy(Integer currentYearOfStudy) {
        this.currentYearOfStudy = currentYearOfStudy;
    }

    public Integer getYearOfStudy() {
        return currentYearOfStudy; // Compatibility method
    }

    public void setYearOfStudy(Integer yearOfStudy) {
        this.currentYearOfStudy = yearOfStudy; // Compatibility method
    }

    public Integer getEnrollmentYear() {
        return enrollmentYear;
    }

    public void setEnrollmentYear(Integer enrollmentYear) {
        this.enrollmentYear = enrollmentYear;
    }

    public java.time.LocalDate getEnrollmentDate() {
        return enrollmentDate;
    }

    public void setEnrollmentDate(java.time.LocalDate enrollmentDate) {
        this.enrollmentDate = enrollmentDate;
    }

    public Boolean isActive() {
        return isActive;
    }

    public void setActive(Boolean isActive) {
        this.isActive = isActive;
    }
}