package rs.ac.uns.acs.nais.ColumnarDatabaseService.dto;

import java.time.LocalDate;

/**
 * DTO za osnovne informacije o studentu
 */
public class StudentDTO {
    private Long studentId;
    private String firstName;
    private String lastName;
    private String indexNumber;
    private String studyProgram;
    private Integer currentYearOfStudy;
    private LocalDate enrollmentDate;
    private Boolean isActive;

    // Konstruktori
    public StudentDTO() {}

    public StudentDTO(Long studentId, String firstName, String lastName, String indexNumber, 
                     String studyProgram, Integer currentYearOfStudy, LocalDate enrollmentDate, Boolean isActive) {
        this.studentId = studentId;
        this.firstName = firstName;
        this.lastName = lastName;
        this.indexNumber = indexNumber;
        this.studyProgram = studyProgram;
        this.currentYearOfStudy = currentYearOfStudy;
        this.enrollmentDate = enrollmentDate;
        this.isActive = isActive;
    }

    // Getters i Setters
    public Long getStudentId() { return studentId; }
    public void setStudentId(Long studentId) { this.studentId = studentId; }

    public String getFirstName() { return firstName; }
    public void setFirstName(String firstName) { this.firstName = firstName; }

    public String getLastName() { return lastName; }
    public void setLastName(String lastName) { this.lastName = lastName; }

    public String getIndexNumber() { return indexNumber; }
    public void setIndexNumber(String indexNumber) { this.indexNumber = indexNumber; }

    public String getStudyProgram() { return studyProgram; }
    public void setStudyProgram(String studyProgram) { this.studyProgram = studyProgram; }

    public Integer getCurrentYearOfStudy() { return currentYearOfStudy; }
    public void setCurrentYearOfStudy(Integer currentYearOfStudy) { this.currentYearOfStudy = currentYearOfStudy; }

    public LocalDate getEnrollmentDate() { return enrollmentDate; }
    public void setEnrollmentDate(LocalDate enrollmentDate) { this.enrollmentDate = enrollmentDate; }

    public Boolean getIsActive() { return isActive; }
    public void setIsActive(Boolean isActive) { this.isActive = isActive; }
}