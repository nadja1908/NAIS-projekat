package rs.ac.uns.acs.nais.ColumnarDatabaseService.entity;

import org.springframework.data.cassandra.core.cql.Ordering;
import org.springframework.data.cassandra.core.cql.PrimaryKeyType;
import org.springframework.data.cassandra.core.mapping.Column;
import org.springframework.data.cassandra.core.mapping.PrimaryKeyColumn;
import org.springframework.data.cassandra.core.mapping.Table;

@Table("students_by_program_year")
public class StudentsByProgramYear {

    @PrimaryKeyColumn(name = "study_program", type = PrimaryKeyType.PARTITIONED)
    private String studyProgram;

    @PrimaryKeyColumn(name = "current_year_of_study", ordinal = 0, ordering = Ordering.ASCENDING)
    private Integer currentYearOfStudy;

    @PrimaryKeyColumn(name = "student_id", ordinal = 1)
    private Long studentId;

    @Column("first_name")
    private String firstName;

    @Column("last_name")
    private String lastName;

    @Column("index_number")
    private String indexNumber;

    @Column("enrollment_year")
    private Integer enrollmentYear;

    @Column("is_active")
    private Boolean isActive;

    public StudentsByProgramYear() {}

    // Getters and setters
    public String getStudyProgram() { return studyProgram; }
    public void setStudyProgram(String studyProgram) { this.studyProgram = studyProgram; }
    public Integer getCurrentYearOfStudy() { return currentYearOfStudy; }
    public void setCurrentYearOfStudy(Integer currentYearOfStudy) { this.currentYearOfStudy = currentYearOfStudy; }
    public Long getStudentId() { return studentId; }
    public void setStudentId(Long studentId) { this.studentId = studentId; }
    public String getFirstName() { return firstName; }
    public void setFirstName(String firstName) { this.firstName = firstName; }
    public String getLastName() { return lastName; }
    public void setLastName(String lastName) { this.lastName = lastName; }
    public String getIndexNumber() { return indexNumber; }
    public void setIndexNumber(String indexNumber) { this.indexNumber = indexNumber; }
    public Integer getEnrollmentYear() { return enrollmentYear; }
    public void setEnrollmentYear(Integer enrollmentYear) { this.enrollmentYear = enrollmentYear; }
    public Boolean getIsActive() { return isActive; }
    public void setIsActive(Boolean isActive) { this.isActive = isActive; }
}
