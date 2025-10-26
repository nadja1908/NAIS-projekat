package rs.ac.uns.acs.nais.ColumnarDatabaseService.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import rs.ac.uns.acs.nais.ColumnarDatabaseService.entity.GradesBySubjectYear;
import rs.ac.uns.acs.nais.ColumnarDatabaseService.repository.GradesBySubjectYearRepository;

import java.util.List;

@Service
public class GradesBySubjectYearService {

    @Autowired
    private GradesBySubjectYearRepository repository;

    public GradesBySubjectYear save(GradesBySubjectYear row) { return repository.save(row); }

    public List<GradesBySubjectYear> findBySubjectAndYear(String subjectId, String academicYear) {
        return repository.findBySubjectAndYear(subjectId, academicYear);
    }

    public Double getAverageGrade(String subjectId, String academicYear) {
        return repository.getAverageGradeForSubjectYear(subjectId, academicYear);
    }
}
