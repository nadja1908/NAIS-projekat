package rs.ac.uns.acs.nais.RelationalDatabaseService.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import rs.ac.uns.acs.nais.RelationalDatabaseService.model.StudentLookup;
import rs.ac.uns.acs.nais.RelationalDatabaseService.model.Subject;
import rs.ac.uns.acs.nais.RelationalDatabaseService.service.GradeSagaService;

import java.util.Map;

@RestController
@RequestMapping("/api/v1/transactional")
public class TransactionController {

    @Autowired
    private GradeSagaService sagaService;

    /**
     * Example payload:
     * {
     *   "student": {"studentId": 9999, "firstName":"A","lastName":"B","indexNumber":"2025/001"},
     *   "subject": {"subjectId":"CS101","name":"Intro","department":"FTN"},
     *   "grade": { ... DTO fields expected by Columnar /api/v1/student-grades ... }
     * }
     */
    @PostMapping("/grade")
    public ResponseEntity<?> createTransactionalGrade(@RequestBody Map<String, Object> payload) {
        try {
            Map<String, Object> studentMap = (Map<String, Object>) payload.get("student");
            Map<String, Object> subjectMap = (Map<String, Object>) payload.get("subject");
            Map<String, Object> gradeMap = (Map<String, Object>) payload.get("grade");

            StudentLookup student = new StudentLookup();
            student.setStudentId(((Number) studentMap.get("studentId")).longValue());
            student.setFirstName((String) studentMap.get("firstName"));
            student.setLastName((String) studentMap.get("lastName"));
            student.setIndexNumber((String) studentMap.get("indexNumber"));

            Subject subject = new Subject();
            subject.setSubjectId((String) subjectMap.get("subjectId"));
            subject.setName((String) subjectMap.get("name"));
            subject.setDepartment((String) subjectMap.get("department"));

            sagaService.createLookupAndRemoteGrade(student, subject, gradeMap);

            return ResponseEntity.ok(Map.of("status", "ok"));
        } catch (Exception ex) {
            return ResponseEntity.status(500).body(Map.of("status", "error", "message", ex.getMessage()));
        }
    }
}
