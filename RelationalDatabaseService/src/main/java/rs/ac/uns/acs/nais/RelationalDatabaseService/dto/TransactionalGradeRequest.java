package rs.ac.uns.acs.nais.RelationalDatabaseService.dto;

import io.swagger.v3.oas.annotations.media.Schema;

import java.util.Map;

@Schema(description = "Transactional request combining lookup student, subject and grade payload")
public class TransactionalGradeRequest {

    @Schema(description = "Student lookup object", required = true)
    private Map<String, Object> student;

    @Schema(description = "Subject lookup object", required = true)
    private Map<String, Object> subject;

    @Schema(description = "Grade payload to forward to Columnar", required = true)
    private Map<String, Object> grade;

    public Map<String, Object> getStudent() {
        return student;
    }

    public void setStudent(Map<String, Object> student) {
        this.student = student;
    }

    public Map<String, Object> getSubject() {
        return subject;
    }

    public void setSubject(Map<String, Object> subject) {
        this.subject = subject;
    }

    public Map<String, Object> getGrade() {
        return grade;
    }

    public void setGrade(Map<String, Object> grade) {
        this.grade = grade;
    }
}
