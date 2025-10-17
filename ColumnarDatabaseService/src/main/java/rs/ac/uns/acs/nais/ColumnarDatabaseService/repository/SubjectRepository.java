package rs.ac.uns.acs.nais.ColumnarDatabaseService.repository;

import org.springframework.data.cassandra.repository.CassandraRepository;
import org.springframework.data.cassandra.repository.Query;
import org.springframework.stereotype.Repository;
import rs.ac.uns.acs.nais.ColumnarDatabaseService.entity.Subject;

import java.util.List;
import java.util.Optional;

@Repository
public interface SubjectRepository extends CassandraRepository<Subject, String> {

    // Osnovni CRUD
    Optional<Subject> findBySubjectId(String subjectId);

    // Pretraga po departmanu
    @Query("SELECT * FROM subject WHERE department = ?0 ALLOW FILTERING")
    List<Subject> findByDepartment(String department);

    // Predmeti po profesoru
    @Query("SELECT * FROM subject WHERE professor_id = ?0 ALLOW FILTERING")
    List<Subject> findByProfessorId(Long professorId);

    // Predmeti po godini studija
    @Query("SELECT * FROM subject WHERE year_of_study = ?0 ALLOW FILTERING")
    List<Subject> findByYearOfStudy(Integer yearOfStudy);

    // Predmeti po semestru
    @Query("SELECT * FROM subject WHERE semester = ?0 ALLOW FILTERING")
    List<Subject> findBySemester(Integer semester);

    // Kombinovane pretrage
    @Query("SELECT * FROM subject WHERE department = ?0 AND year_of_study = ?1 ALLOW FILTERING")
    List<Subject> findByDepartmentAndYear(String department, Integer yearOfStudy);

    @Query("SELECT * FROM subject WHERE department = ?0 AND semester = ?1 ALLOW FILTERING")
    List<Subject> findByDepartmentAndSemester(String department, Integer semester);

    @Query("SELECT * FROM subject WHERE year_of_study = ?0 AND semester = ?1 ALLOW FILTERING")
    List<Subject> findByYearAndSemester(Integer yearOfStudy, Integer semester);

    // Predmeti po ECTS bodovima
    @Query("SELECT * FROM subject WHERE ects_points >= ?0 ALLOW FILTERING")
    List<Subject> findByMinEctsPoints(Integer minEcts);

    @Query("SELECT * FROM subject WHERE ects_points = ?0 ALLOW FILTERING")
    List<Subject> findByEctsPoints(Integer ectsPoints);

    // Analitički upiti
    @Query("SELECT COUNT(*) FROM subject WHERE department = ?0")
    Long countByDepartment(String department);

    @Query("SELECT SUM(ects_points) FROM subject WHERE year_of_study = ?0")
    Integer getTotalEctsByYear(Integer yearOfStudy);

    @Query("SELECT AVG(ects_points) FROM subject WHERE department = ?0")
    Double getAverageEctsByDepartment(String department);

    // Pretraga po imenu predmeta
    @Query("SELECT * FROM subject WHERE subject_name = ?0 ALLOW FILTERING")
    List<Subject> findBySubjectName(String subjectName);

    // Obavezni vs izborni
    @Query("SELECT * FROM subject WHERE is_mandatory = ?0 ALLOW FILTERING")
    List<Subject> findByMandatoryStatus(Boolean isMandatory);

    @Query("SELECT * FROM subject WHERE department = ?0 AND is_mandatory = ?1 ALLOW FILTERING")
    List<Subject> findByDepartmentAndMandatoryStatus(String department, Boolean isMandatory);
}