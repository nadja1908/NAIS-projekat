package rs.ac.uns.acs.nais.ColumnarDatabaseService.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import rs.ac.uns.acs.nais.ColumnarDatabaseService.dto.SubjectDTO;
import rs.ac.uns.acs.nais.ColumnarDatabaseService.entity.Subject;
import rs.ac.uns.acs.nais.ColumnarDatabaseService.service.SubjectService;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/v1/subjects")
@CrossOrigin(origins = "*")
public class SubjectController {

    @Autowired
    private SubjectService subjectService;

    // CREATE
    @PostMapping
    public ResponseEntity<SubjectDTO> createSubject(@RequestBody SubjectDTO dto) {
        Subject subject = convertToEntity(dto);
        Subject saved = subjectService.saveSubject(subject);
        return ResponseEntity.ok(convertToDTO(saved));
    }

    // READ all
    @GetMapping
    public ResponseEntity<List<SubjectDTO>> getAllSubjects() {
        List<Subject> subjects = subjectService.getAllSubjects();
        List<SubjectDTO> dtos = subjects.stream().map(this::convertToDTO).collect(Collectors.toList());
        return ResponseEntity.ok(dtos);
    }

    // READ by id
    @GetMapping("/{id}")
    public ResponseEntity<SubjectDTO> getSubjectById(@PathVariable("id") String id) {
        return subjectService.getSubjectById(id)
                .map(subject -> ResponseEntity.ok(convertToDTO(subject)))
                .orElse(ResponseEntity.notFound().build());
    }

    // UPDATE
    @PutMapping("/{id}")
    public ResponseEntity<SubjectDTO> updateSubject(@PathVariable("id") String id, @RequestBody SubjectDTO dto) {
        if (!subjectService.doesSubjectExist(id)) {
            return ResponseEntity.notFound().build();
        }
        Subject subj = convertToEntity(dto);
        subj.setSubjectId(id);
        Subject updated = subjectService.saveSubject(subj);
        return ResponseEntity.ok(convertToDTO(updated));
    }

    // DELETE
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteSubject(@PathVariable("id") String id) {
        if (!subjectService.doesSubjectExist(id)) {
            return ResponseEntity.notFound().build();
        }
        subjectService.deleteSubject(id);
        return ResponseEntity.noContent().build();
    }

    // Helper conversions
    private Subject convertToEntity(SubjectDTO dto) {
    return new Subject(
        dto.getSubjectId(),
        dto.getSubjectName(),
        dto.getDepartment(),
        dto.getEctsPoints(),
        dto.getYearOfStudy(),
        dto.getSemester(),
        dto.getProfessorId(),
        dto.getProfessorName(),
        dto.getMandatory()
    );
    }

    private SubjectDTO convertToDTO(Subject subject) {
    return new SubjectDTO(
        subject.getSubjectId(),
        subject.getSubjectName(),
        subject.getDepartment(),
        subject.getEctsPoints(),
        subject.getYearOfStudy(),
        subject.getSemester(),
        subject.getProfessorId(),
        subject.getProfessorName(),
        subject.isMandatory()
    );
    }
}
