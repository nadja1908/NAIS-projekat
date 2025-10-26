package rs.ac.uns.acs.nais.ColumnarDatabaseService.repository;

import org.springframework.data.cassandra.repository.CassandraRepository;
import org.springframework.data.cassandra.repository.Query;
import org.springframework.stereotype.Repository;
import rs.ac.uns.acs.nais.ColumnarDatabaseService.entity.GradesBySubjectYear;

import java.util.List;

@Repository
public interface GradesBySubjectYearRepository extends CassandraRepository<GradesBySubjectYear, String> {

    @Query("SELECT * FROM grades_by_subject_year WHERE subject_id = ?0 AND academic_year = ?1")
    List<GradesBySubjectYear> findBySubjectAndYear(String subjectId, String academicYear);

    @Query("SELECT AVG(grade) FROM grades_by_subject_year WHERE subject_id = ?0 AND academic_year = ?1")
    Double getAverageGradeForSubjectYear(String subjectId, String academicYear);
}
