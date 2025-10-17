package rs.ac.uns.acs.nais.ColumnarDatabaseService.repository;

import org.springframework.data.cassandra.repository.CassandraRepository;
import org.springframework.data.cassandra.repository.Query;
import org.springframework.stereotype.Repository;
import rs.ac.uns.acs.nais.ColumnarDatabaseService.entity.ProfessorPerformance;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProfessorPerformanceRepository extends CassandraRepository<ProfessorPerformance, Long> {

    // CRUD operacije po profesoru
    @Query("SELECT * FROM professor_performance WHERE professor_id = ?0")
    List<ProfessorPerformance> findByProfessorId(Long professorId);

    @Query("SELECT * FROM professor_performance WHERE professor_id = ?0 AND academic_year = ?1")
    Optional<ProfessorPerformance> findByProfessorIdAndYear(Long professorId, String academicYear);

    // SLOŽENI UPITI - Analiza performansi

    // 1. Top profesori po prolaznosti studenata
    @Query("SELECT * FROM professor_performance WHERE overall_pass_rate > ?0 ALLOW FILTERING")
    List<ProfessorPerformance> findProfessorsWithHighPassRate(Double passRateThreshold);

    // 2. Profesori sa niskim prolaznostima - potrebna pomoć
    @Query("SELECT * FROM professor_performance WHERE overall_pass_rate < ?0 ALLOW FILTERING")
    List<ProfessorPerformance> findProfessorsNeedingSupport(Double lowPassRateThreshold);

    // 3. Profesori sa najvišim prosečnim ocenama
    @Query("SELECT * FROM professor_performance WHERE average_grade_given > ?0 ALLOW FILTERING")
    List<ProfessorPerformance> findProfessorsWithHighGrades(Double gradeThreshold);

    // 4. Analiza opterećenosti profesora
    @Query("SELECT * FROM professor_performance WHERE total_students_taught > ?0 ALLOW FILTERING")
    List<ProfessorPerformance> findOverloadedProfessors(Integer studentCountThreshold);

    // 5. Profesori sa najviše različitih predmeta
    @Query("SELECT * FROM professor_performance WHERE total_subjects_taught > ?0 ALLOW FILTERING")
    List<ProfessorPerformance> findVersatileProfessors(Integer subjectCountThreshold);

    // 6. Trending po godinama - poboljšanje/pogoršanje
    @Query("SELECT * FROM professor_performance WHERE professor_id = ?0")
    List<ProfessorPerformance> findProfessorPerformanceTrends(Long professorId);

    // 7. Profesori sa problematičnim predmetima
    @Query("SELECT * FROM professor_performance WHERE hardest_subject_pass_rate < ?0 ALLOW FILTERING")
    List<ProfessorPerformance> findProfessorsWithDifficultSubjects(Double hardSubjectThreshold);

    // 8. Profesori sa konzistentnim performansama
    @Query("SELECT * FROM professor_performance WHERE performance_consistency_score > ?0 ALLOW FILTERING")
    List<ProfessorPerformance> findConsistentProfessors(Double consistencyThreshold);

    // 9. Sve performanse za određenu godinu
    @Query("SELECT * FROM professor_performance WHERE academic_year = ?0 ALLOW FILTERING")
    List<ProfessorPerformance> findAllPerformancesByYear(String academicYear);

    // 10. Pronađi najbolje profesore po departmanu/predmetu
    @Query("SELECT * FROM professor_performance WHERE easiest_subject_id = ?0 ALLOW FILTERING")
    List<ProfessorPerformance> findProfessorsByEasiestSubject(String subjectId);

    // 11. Prosečna prolaznost svih profesora za godinu
    @Query("SELECT AVG(overall_pass_rate) FROM professor_performance WHERE academic_year = ?0")
    Double getAveragePassRateForYear(String academicYear);

    // 12. Count profesora sa specifičnim kriterijumima
    @Query("SELECT COUNT(*) FROM professor_performance WHERE academic_year = ?0 AND overall_pass_rate > ?1")
    Long countSuccessfulProfessors(String academicYear, Double passRateThreshold);
}