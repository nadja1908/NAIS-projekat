package rs.ac.uns.acs.nais.RelationalDatabaseService.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Student lookup DTO")
public class StudentDto {
    @Schema(description = "Business student identifier (index or student number)", example = "1111")
    private Long studentId;

    @Schema(example = "Test")
    private String firstName;

    @Schema(example = "Student")
    private String lastName;

    @Schema(example = "2025/001")
    private String indexNumber;

    public Long getStudentId() { return studentId; }
    public void setStudentId(Long studentId) { this.studentId = studentId; }
    public String getFirstName() { return firstName; }
    public void setFirstName(String firstName) { this.firstName = firstName; }
    public String getLastName() { return lastName; }
    public void setLastName(String lastName) { this.lastName = lastName; }
    public String getIndexNumber() { return indexNumber; }
    public void setIndexNumber(String indexNumber) { this.indexNumber = indexNumber; }
}
