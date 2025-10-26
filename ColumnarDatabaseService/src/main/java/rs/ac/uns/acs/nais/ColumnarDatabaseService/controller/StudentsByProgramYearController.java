package rs.ac.uns.acs.nais.ColumnarDatabaseService.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import rs.ac.uns.acs.nais.ColumnarDatabaseService.entity.StudentsByProgramYear;
import rs.ac.uns.acs.nais.ColumnarDatabaseService.service.StudentsByProgramYearService;

import java.util.List;

@RestController
@RequestMapping("/api/v1/students-by-program")
@CrossOrigin(origins = "*")
@io.swagger.v3.oas.annotations.tags.Tag(name = "StudentsByProgramYear", description = "Denormalized access for students grouped by program and year")
public class StudentsByProgramYearController {

    @Autowired
    private StudentsByProgramYearService service;

    @PostMapping
    @io.swagger.v3.oas.annotations.Operation(summary = "Create denormalized students-by-program row")
    public ResponseEntity<StudentsByProgramYear> create(@RequestBody StudentsByProgramYear row) {
        return ResponseEntity.ok(service.save(row));
    }

    @GetMapping("/program/{program}")
    public ResponseEntity<List<StudentsByProgramYear>> getByProgramAndYear(@PathVariable String program,
                                                                           @RequestParam Integer year) {
        return ResponseEntity.ok(service.findByProgramAndYear(program, year));
    }

    @GetMapping("/program/{program}/count")
    public ResponseEntity<Long> countByProgramAndYear(@PathVariable String program,
                                                      @RequestParam Integer year) {
        return ResponseEntity.ok(service.countByProgramAndYear(program, year));
    }
}
