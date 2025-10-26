package rs.ac.uns.acs.nais.ColumnarDatabaseService.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import rs.ac.uns.acs.nais.ColumnarDatabaseService.entity.StudentsByProgramYear;
import rs.ac.uns.acs.nais.ColumnarDatabaseService.repository.StudentsByProgramYearRepository;

import java.util.List;

@Service
public class StudentsByProgramYearService {

    @Autowired
    private StudentsByProgramYearRepository repository;

    public StudentsByProgramYear save(StudentsByProgramYear row) { return repository.save(row); }

    public List<StudentsByProgramYear> findByProgramAndYear(String program, Integer year) {
        return repository.findByProgramAndYear(program, year);
    }

    public Long countByProgramAndYear(String program, Integer year) {
        return repository.countByProgramAndYear(program, year);
    }
}
