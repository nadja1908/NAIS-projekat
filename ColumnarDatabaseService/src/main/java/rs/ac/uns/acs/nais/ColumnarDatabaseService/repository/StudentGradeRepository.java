package rs.ac.uns.acs.nais.ColumnarDatabaseService.repository;

import org.springframework.data.cassandra.repository.CassandraRepository;
import org.springframework.data.cassandra.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import rs.ac.uns.acs.nais.ColumnarDatabaseService.entity.StudentGrade;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface StudentGradeRepository extends CassandraRepository<StudentGrade, Long> {

    // CRUD operacije po studentu
    @Query("SELECT * FROM student_grades WHERE student_id = ?0")
    List<StudentGrade> findByStudentId(Long studentId);

    @Query("SELECT * FROM student_grades WHERE student_id = ?0 AND academic_year = ?1")
    List<StudentGrade> findByStudentIdAndAcademicYear(Long studentId, String academicYear);

    @Query("SELECT * FROM student_grades WHERE student_id = ?0 AND subject_id = ?1")
    List<StudentGrade> findByStudentIdAndSubjectId(Long studentId, String subjectId);

    // SLOŽENI UPITI - Grupisanje i agregiranje

    // 1. Prosečna ocena studenta po godini
    @Query("SELECT AVG(grade) FROM student_grades WHERE student_id = ?0 AND academic_year = ?1 AND passed = true ALLOW FILTERING")
    Double getAverageGradeByStudentAndYear(Long studentId, String academicYear);

    // 2. Broj položenih/pao studenata po predmetu
    @Query("SELECT COUNT(*) FROM student_grades WHERE subject_id = ?0 AND passed = true ALLOW FILTERING")
    Long countPassedStudentsBySubject(String subjectId);

    @Query("SELECT COUNT(*) FROM student_grades WHERE subject_id = ?0 AND passed = false ALLOW FILTERING")
    Long countFailedStudentsBySubject(String subjectId);

    // 3. Studenti sa ocenom ispod određenog praga u tekućoj godini
    @Query("SELECT * FROM student_grades WHERE academic_year = ?0 AND grade < ?1 AND passed = true ALLOW FILTERING")
    List<StudentGrade> findStudentsWithGradeBelowThreshold(String academicYear, Integer gradeThreshold);

    // 4. Prosečna ocena po departmanu
    @Query("SELECT AVG(grade) FROM student_grades WHERE department = ?0 AND passed = true ALLOW FILTERING")
    Double getAverageGradeByDepartment(String department);

    // 5. Najteži predmeti (najniža prolaznost)
    @Query("SELECT * FROM student_grades WHERE academic_year = ?0 ALLOW FILTERING")
    List<StudentGrade> findAllGradesByAcademicYear(String academicYear);

    // 6. Studenti koji nisu položili predmet u tekućoj godini
    @Query("SELECT * FROM student_grades WHERE academic_year = ?0 AND passed = false ALLOW FILTERING")
    List<StudentGrade> findFailedStudentsInCurrentYear(String academicYear);

    // 7. Ocene po profesoru u određenoj godini
    @Query("SELECT * FROM student_grades WHERE professor_id = ?0 AND academic_year = ?1 ALLOW FILTERING")
    List<StudentGrade> findGradesByProfessorAndYear(Long professorId, String academicYear);

    // 8. Predmeti sa prolaznosću ispod određenog procenta
    @Query("SELECT * FROM student_grades WHERE subject_id = ?0 AND academic_year = ?1")
    List<StudentGrade> findGradesBySubjectAndYear(String subjectId, String academicYear);

    // 9. Statistike po semestru
    @Query("SELECT * FROM student_grades WHERE semester = ?0 AND academic_year = ?1 ALLOW FILTERING")
    List<StudentGrade> findGradesBySemesterAndYear(Integer semester, String academicYear);

    // 10. Ocene u određenom vremenskom opsegu
    @Query("SELECT * FROM student_grades WHERE student_id = ?0 AND exam_date >= ?1 AND exam_date <= ?2 ALLOW FILTERING")
    List<StudentGrade> findGradesByStudentAndDateRange(Long studentId, LocalDateTime startDate, LocalDateTime endDate);

    // === DODATNE METODE ZA SERVICE LAYER ===

    // Prosečna ocena studenta
    @Query("SELECT AVG(grade) FROM student_grades WHERE student_id = ?0 AND passed = true ALLOW FILTERING")
    Double getAverageGradeByStudent(Long studentId);

    // Broj položenih ocena po studentu
    @Query("SELECT COUNT(*) FROM student_grades WHERE student_id = ?0 AND passed = true ALLOW FILTERING")
    Long getPassedGradesCountByStudent(Long studentId);

    // Broj nepoloženih ocena po studentu
    @Query("SELECT COUNT(*) FROM student_grades WHERE student_id = ?0 AND passed = false ALLOW FILTERING")
    Long getFailedGradesCountByStudent(Long studentId);

    // Svi ispiti po akademskoj godini
    @Query("SELECT * FROM student_grades WHERE academic_year = ?0 ALLOW FILTERING")
    List<StudentGrade> findByAcademicYear(String academicYear);

    // Studenti sa niskim ocenama
    @Query("SELECT * FROM student_grades WHERE grade < ?0 AND passed = true ALLOW FILTERING")
    List<StudentGrade> findStudentsWithLowGrades(Double gradeThreshold);

    // Ispiti po predmetu i godini
    @Query("SELECT * FROM student_grades WHERE subject_id = ?0 AND academic_year = ?1 ALLOW FILTERING")
    List<StudentGrade> findBySubjectIdAndYear(String subjectId, String academicYear);

    // Svi ispiti po predmetu
    @Query("SELECT * FROM student_grades WHERE subject_id = ?0 ALLOW FILTERING")
    List<StudentGrade> findBySubjectId(String subjectId);

    // Ispiti po departmanu i godini
    @Query("SELECT * FROM student_grades WHERE department = ?0 AND academic_year = ?1 ALLOW FILTERING")
    List<StudentGrade> findByDepartmentAndYear(String department, String academicYear);

    // Ispiti po profesoru i godini
    @Query("SELECT * FROM student_grades WHERE professor_id = ?0 AND academic_year = ?1 ALLOW FILTERING")
    List<StudentGrade> findByProfessorIdAndYear(Long professorId, String academicYear);

    // Ispiti u određenom vremenskom opsegu
    @Query("SELECT * FROM student_grades WHERE exam_date >= ?0 AND exam_date <= ?1 ALLOW FILTERING")
    List<StudentGrade> findByExamDateBetween(LocalDateTime startDate, LocalDateTime endDate);
}