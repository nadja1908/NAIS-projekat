package rs.ac.uns.acs.nais.ColumnarDatabaseService.repository;

import org.springframework.data.cassandra.repository.CassandraRepository;
import org.springframework.data.cassandra.repository.Query;
import org.springframework.stereotype.Repository;
import rs.ac.uns.acs.nais.ColumnarDatabaseService.entity.Student;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface StudentRepository extends CassandraRepository<Student, Long> {

    // Osnovni CRUD
    Optional<Student> findByStudentId(Long studentId);
    
    @Query("SELECT * FROM students WHERE index_number = ?0 ALLOW FILTERING")
    Optional<Student> findByIndexNumber(String indexNumber);

    // Pretraga po programima studija
    @Query("SELECT * FROM students WHERE study_program = ?0 ALLOW FILTERING")
    List<Student> findByStudyProgram(String studyProgram);

    // Aktivni studenti
    @Query("SELECT * FROM students WHERE is_active = true ALLOW FILTERING")
    List<Student> findActiveStudents();

    // Studenti po godini upisa
    @Query("SELECT * FROM students WHERE enrollment_date >= ?0 AND enrollment_date <= ?1 ALLOW FILTERING")
    List<Student> findByEnrollmentDateBetween(LocalDate startDate, LocalDate endDate);

    // Studenti po godini studija
    @Query("SELECT * FROM students WHERE current_year_of_study = ?0 ALLOW FILTERING")
    List<Student> findByCurrentYear(Integer year);

    // Pretraga po imenu (parcijalno)
    @Query("SELECT * FROM students WHERE first_name = ?0 ALLOW FILTERING")
    List<Student> findByFirstName(String firstName);

    @Query("SELECT * FROM students WHERE last_name = ?0 ALLOW FILTERING")
    List<Student> findByLastName(String lastName);

    // Kombinovane pretrage
    @Query("SELECT * FROM students WHERE study_program = ?0 AND current_year_of_study = ?1 ALLOW FILTERING")
    List<Student> findByStudyProgramAndYear(String studyProgram, Integer year);

    @Query("SELECT * FROM students WHERE study_program = ?0 AND is_active = ?1 ALLOW FILTERING")
    List<Student> findByStudyProgramAndStatus(String studyProgram, Boolean isActive);

    // Brojanje
    @Query("SELECT COUNT(*) FROM students WHERE study_program = ?0")
    Long countByStudyProgram(String studyProgram);

    @Query("SELECT COUNT(*) FROM students WHERE is_active = true")
    Long countActiveStudents();
}