package rs.ac.uns.acs.nais.ColumnarDatabaseService.repository;

import org.springframework.data.cassandra.repository.CassandraRepository;
import org.springframework.data.cassandra.repository.Query;
import org.springframework.stereotype.Repository;
import rs.ac.uns.acs.nais.ColumnarDatabaseService.entity.SubjectStatistics;

import java.util.List;

@Repository
public interface SubjectStatisticsRepository extends CassandraRepository<SubjectStatistics, String> {

    // CRUD operacije po departmanu
    @Query("SELECT * FROM subject_statistics WHERE department = ?0")
    List<SubjectStatistics> findByDepartment(String department);

    @Query("SELECT * FROM subject_statistics WHERE department = ?0 AND academic_year = ?1")
    List<SubjectStatistics> findByDepartmentAndYear(String department, String academicYear);

    @Query("SELECT * FROM subject_statistics WHERE department = ?0 AND subject_id = ?1")
    List<SubjectStatistics> findByDepartmentAndSubject(String department, String subjectId);

    // SLOŽENI UPITI - Grupisanje i agregiranje

    // 1. Top 10 najlakših predmeta po departmanu (najviša prolaznost)
    @Query("SELECT * FROM subject_statistics WHERE department = ?0 AND academic_year = ?1")
    List<SubjectStatistics> findTopSubjectsByDepartmentAndYear(String department, String academicYear);

    // 2. Predmeti sa prolaznosću ispod određenog procenta
    @Query("SELECT * FROM subject_statistics WHERE department = ?0 AND pass_rate_percentage < ?1 ALLOW FILTERING")
    List<SubjectStatistics> findSubjectsWithLowPassRate(String department, Double passRateThreshold);

    // 3. Prosečna prolaznost po departmanu
    @Query("SELECT AVG(pass_rate_percentage) FROM subject_statistics WHERE department = ?0 AND academic_year = ?1")
    Double getAveragePassRateByDepartment(String department, String academicYear);

    // 4. Najteži predmeti po godini studija
    @Query("SELECT * FROM subject_statistics WHERE department = ?0 AND year_of_study = ?1 ALLOW FILTERING")
    List<SubjectStatistics> findSubjectsByDepartmentAndYearOfStudy(String department, Integer yearOfStudy);

    // 5. Statistike po profesoru
    @Query("SELECT * FROM subject_statistics WHERE department = ?0 AND professor_id = ?1 ALLOW FILTERING")
    List<SubjectStatistics> findSubjectsByDepartmentAndProfessor(String department, Long professorId);

    // 6. Predmeti sa visokim prosečnim ocenama
    @Query("SELECT * FROM subject_statistics WHERE department = ?0 AND average_grade > ?1 ALLOW FILTERING")
    List<SubjectStatistics> findSubjectsWithHighAverageGrade(String department, Double gradeThreshold);

    // 7. Poređenje različitih godina
    @Query("SELECT * FROM subject_statistics WHERE department = ?0 AND subject_id = ?1")
    List<SubjectStatistics> findSubjectTrendsOverYears(String department, String subjectId);

    // 8. Najteži predmeti za ceo fakultet u godini
    @Query("SELECT * FROM subject_statistics WHERE academic_year = ?0 ALLOW FILTERING")
    List<SubjectStatistics> findAllSubjectStatisticsByYear(String academicYear);

    // 9. Predmeti sa najviše studenata
    @Query("SELECT * FROM subject_statistics WHERE department = ?0 AND total_enrolled_students > ?1 ALLOW FILTERING")
    List<SubjectStatistics> findSubjectsWithHighEnrollment(String department, Integer enrollmentThreshold);

    // 10. Semestralne statistike
    @Query("SELECT * FROM subject_statistics WHERE department = ?0 AND semester = ?1 ALLOW FILTERING")
    List<SubjectStatistics> findSubjectsBySemester(String department, Integer semester);

    // ADD: Method for Saga Orchestrator
        @Query("SELECT * FROM subject_statistics WHERE subject_id = ?0 AND department = ?1 AND academic_year = ?2 ALLOW FILTERING")
    List<SubjectStatistics> findBySubjectIdAndDepartmentAndYear(String subjectId, String department, String academicYear);
}