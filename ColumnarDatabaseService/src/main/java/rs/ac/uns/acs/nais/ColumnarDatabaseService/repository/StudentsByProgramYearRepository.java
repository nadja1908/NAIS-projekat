package rs.ac.uns.acs.nais.ColumnarDatabaseService.repository;

import org.springframework.data.cassandra.repository.CassandraRepository;
import org.springframework.data.cassandra.repository.Query;
import org.springframework.stereotype.Repository;
import rs.ac.uns.acs.nais.ColumnarDatabaseService.entity.StudentsByProgramYear;

import java.util.List;

@Repository
public interface StudentsByProgramYearRepository extends CassandraRepository<StudentsByProgramYear, Long> {

    @Query("SELECT * FROM students_by_program_year WHERE study_program = ?0 AND current_year_of_study = ?1")
    List<StudentsByProgramYear> findByProgramAndYear(String studyProgram, Integer year);

    @Query("SELECT COUNT(*) FROM students_by_program_year WHERE study_program = ?0 AND current_year_of_study = ?1")
    Long countByProgramAndYear(String studyProgram, Integer year);
}
