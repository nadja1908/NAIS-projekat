package rs.ac.uns.acs.nais.RelationalDatabaseService.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import rs.ac.uns.acs.nais.RelationalDatabaseService.model.StudentLookup;
import rs.ac.uns.acs.nais.RelationalDatabaseService.model.Subject;
import rs.ac.uns.acs.nais.RelationalDatabaseService.repository.StudentLookupRepository;
import rs.ac.uns.acs.nais.RelationalDatabaseService.repository.SubjectRepository;
import org.springframework.beans.factory.annotation.Value;

import java.util.HashMap;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Service
public class GradeSagaService {

    private static final Logger log = LoggerFactory.getLogger(GradeSagaService.class);

    @Autowired
    private StudentLookupRepository studentLookupRepository;

    @Autowired
    private SubjectRepository subjectRepository;

    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private RestTemplate simpleRestTemplate;

    // URL of columnar service - can point to eureka service name or gateway; configured via application.properties
    @Value("${columnar.service.url:http://ColumnarDatabaseService}")
    private String columnarServiceUrl;

    private String getColumnarGradesUrl() {
        // ensure trailing path
        return columnarServiceUrl.endsWith("/") ? columnarServiceUrl + "api/v1/student-grades" : columnarServiceUrl + "/api/v1/student-grades";
    }

    @Transactional
    public void createLookupAndRemoteGrade(StudentLookup student, Subject subject, Map<String, Object> gradeDto) {
        // Try to find existing lookup entries; create only if missing
        StudentLookup persistedStudent = null;
        if (student != null && student.getStudentId() != null) {
            persistedStudent = studentLookupRepository.findByStudentId(student.getStudentId()).orElse(null);
        }
        if (persistedStudent == null && student != null) {
            persistedStudent = studentLookupRepository.save(student);
        }

        Subject persistedSubject = null;
        if (subject != null && subject.getSubjectId() != null) {
            persistedSubject = subjectRepository.findBySubjectId(subject.getSubjectId()).orElse(null);
        }
        if (persistedSubject == null && subject != null) {
            persistedSubject = subjectRepository.save(subject);
        }

        // Enrich/normalize grade DTO to ensure Columnar receives required fields
        Map<String, Object> outgoing = new HashMap<>(gradeDto != null ? gradeDto : Map.of());

        // Ensure studentId (Long) and subjectId (String) are present and come from lookup
        // Use persisted lookup identifiers (prefer already-stored entities)
        if (persistedStudent != null && persistedStudent.getStudentId() != null) {
            outgoing.put("studentId", persistedStudent.getStudentId());
        } else if (student != null && student.getStudentId() != null) {
            outgoing.put("studentId", student.getStudentId());
        }

        if (persistedSubject != null && persistedSubject.getSubjectId() != null) {
            outgoing.put("subjectId", persistedSubject.getSubjectId());
        } else if (subject != null && subject.getSubjectId() != null) {
            outgoing.put("subjectId", subject.getSubjectId());
        }

        // If department missing, prefer the value from subject lookup
        if (!outgoing.containsKey("department")) {
            if (persistedSubject != null && persistedSubject.getDepartment() != null) {
                outgoing.put("department", persistedSubject.getDepartment());
            } else if (subject != null && subject.getDepartment() != null) {
                outgoing.put("department", subject.getDepartment());
            }
        }

        // Normalize numeric 'grade' to Double (Columnar DTO expects Double)
        if (outgoing.containsKey("grade")) {
            Object g = outgoing.get("grade");
            if (g instanceof Number) {
                outgoing.put("grade", ((Number) g).doubleValue());
            }
        }

        // call remote columnar service to create grade
        String targetUrl = getColumnarGradesUrl();
        log.info("Posting grade to Columnar at {}. Payload: {}", targetUrl, outgoing);

        // If the configured columnar.service.url is an absolute URL (contains scheme), prefer a direct container call
        boolean isAbsolute = columnarServiceUrl != null && (columnarServiceUrl.startsWith("http://") || columnarServiceUrl.startsWith("https://"));
        if (isAbsolute) {
            try {
                log.info("Attempting direct call to Columnar (simple RestTemplate) to {}", targetUrl);
                simpleRestTemplate.postForEntity(targetUrl, outgoing, String.class);
                log.info("Direct call succeeded");
                return;
            } catch (RestClientException ex) {
                log.warn("Direct call to Columnar failed: {}", ex.getMessage());
                // fall through and attempt load-balanced call as a fallback
            }
        }

        // Try using the load-balanced RestTemplate (Eureka / service discovery)
        try {
            log.info("Attempting load-balanced call to Columnar (via RestTemplate)");
            restTemplate.postForEntity(targetUrl, outgoing, String.class);
            log.info("Load-balanced call succeeded");
            return;
        } catch (RestClientException ex) {
            log.warn("Load-balanced call to Columnar failed: {}", ex.getMessage());
            // If we haven't already tried a direct absolute URL (or that attempt failed), try a simple rest template once
            if (isAbsolute) {
                try {
                    log.info("Attempting direct call to Columnar (simple RestTemplate) to {}", targetUrl);
                    simpleRestTemplate.postForEntity(targetUrl, outgoing, String.class);
                    log.info("Direct call succeeded");
                    return;
                } catch (RestClientException ex2) {
                    log.error("Direct call to Columnar failed as well: {}", ex2.getMessage(), ex2);
                    throw new RuntimeException("Remote call to Columnar service failed (both LB and direct): " + ex2.getMessage(), ex2);
                }
            }

            // otherwise rethrow to cause transaction rollback
            throw new RuntimeException("Remote call to Columnar service failed: " + ex.getMessage(), ex);
        }
    }
}
