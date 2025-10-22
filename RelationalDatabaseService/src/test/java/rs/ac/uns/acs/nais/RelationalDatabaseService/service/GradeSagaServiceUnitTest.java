package rs.ac.uns.acs.nais.RelationalDatabaseService.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.web.client.RestTemplate;
import rs.ac.uns.acs.nais.RelationalDatabaseService.model.StudentLookup;
import rs.ac.uns.acs.nais.RelationalDatabaseService.model.Subject;
import rs.ac.uns.acs.nais.RelationalDatabaseService.repository.StudentLookupRepository;
import rs.ac.uns.acs.nais.RelationalDatabaseService.repository.SubjectRepository;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import static org.hamcrest.Matchers.containsString;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.client.ExpectedCount.once;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.content;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.method;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withStatus;
import static org.springframework.http.HttpMethod.POST;
import static org.springframework.http.HttpStatus.CREATED;

public class GradeSagaServiceUnitTest {

    @Test
    public void createLookupAndRemoteGrade_savesLookupsAndPostsToColumnar() throws Exception {
        // mocks for repositories
        StudentLookupRepository studentRepo = mock(StudentLookupRepository.class);
        SubjectRepository subjectRepo = mock(SubjectRepository.class);

        // when findByStudentId/SubjectId called, return empty so save() will be used
        when(studentRepo.findByStudentId(1111L)).thenReturn(Optional.empty());
        when(subjectRepo.findBySubjectId("CS101")).thenReturn(Optional.empty());

        // stub save to return the passed entity
        when(studentRepo.save(any(StudentLookup.class))).thenAnswer(inv -> inv.getArgument(0));
        when(subjectRepo.save(any(Subject.class))).thenAnswer(inv -> inv.getArgument(0));

        // real RestTemplate for simple direct call
        RestTemplate simpleTemplate = new RestTemplate();
        MockRestServiceServer server = MockRestServiceServer.createServer(simpleTemplate);

        // expect a POST to the configured container URL
        String expectedUrl = "http://columnar-key-value-service:9050/api/v1/student-grades";
        server.expect(once(), requestTo(expectedUrl))
                .andExpect(method(POST))
                .andExpect(content().string(containsString("\"studentId\":1111")))
                .andRespond(withStatus(CREATED));

        // create service instance and inject dependencies
        GradeSagaService saga = new GradeSagaService();
        ReflectionTestUtils.setField(saga, "studentLookupRepository", studentRepo);
        ReflectionTestUtils.setField(saga, "subjectRepository", subjectRepo);
        // restTemplate (LB) not used in this test; set to a simple new instance
        ReflectionTestUtils.setField(saga, "restTemplate", new RestTemplate());
        ReflectionTestUtils.setField(saga, "simpleRestTemplate", simpleTemplate);
        // set the configured columnar URL to an absolute container address so service prefers direct call
        ReflectionTestUtils.setField(saga, "columnarServiceUrl", "http://columnar-key-value-service:9050");

        // prepare input objects
        StudentLookup student = new StudentLookup();
        student.setStudentId(1111L);
        student.setFirstName("Test");
        student.setLastName("Student");

        Subject subject = new Subject();
        subject.setSubjectId("CS101");
        subject.setDepartment("FTN");
        subject.setName("CS");

        Map<String,Object> gradeDto = new HashMap<>();
        gradeDto.put("academicYear", "2024/2025");
        gradeDto.put("examDate", LocalDate.of(2025,6,10).toString());
        gradeDto.put("grade", 9);
        gradeDto.put("examType", "FINAL");
        gradeDto.put("professorId", 123);

        // execute
        saga.createLookupAndRemoteGrade(student, subject, gradeDto);

        // verify repos saved
        verify(studentRepo, times(1)).save(any(StudentLookup.class));
        verify(subjectRepo, times(1)).save(any(Subject.class));

        // verify outbound call
        server.verify();
    }
}
