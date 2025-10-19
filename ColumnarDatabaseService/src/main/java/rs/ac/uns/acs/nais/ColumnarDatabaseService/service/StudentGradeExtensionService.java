package rs.ac.uns.acs.nais.ColumnarDatabaseService.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import rs.ac.uns.acs.nais.ColumnarDatabaseService.entity.Student;
import rs.ac.uns.acs.nais.ColumnarDatabaseService.entity.StudentGrade;
import rs.ac.uns.acs.nais.ColumnarDatabaseService.repository.StudentGradeRepository;
import rs.ac.uns.acs.nais.ColumnarDatabaseService.repository.StudentRepository;

import java.util.List;

/**
 * Extension klasa za nedostajuće metode
 */
@Service
public class StudentGradeExtensionService {

    @Autowired
    private StudentGradeRepository studentGradeRepository;
    
    @Autowired
    private StudentRepository studentRepository;

    /**
     * Dobavi ocene sa pragom i godinom
     */
    public List<StudentGrade> getGradesByThresholdAndYear(Double threshold, String academicYear) {
        // Simulacija upita - možeš proširiti repository
        return studentGradeRepository.findByAcademicYear(academicYear)
                .stream()
                .filter(grade -> grade.getGrade() >= threshold)
                .toList();
    }

    /**
     * Dobavi aktivne studente po departmanu
     */
    public List<Student> getActiveStudentsByDepartment(String department) {
        // Simulacija - možeš dodati u StudentRepository
        return studentRepository.findActiveStudents()
                .stream()
                .filter(student -> student.getStudyProgram().contains(department))
                .toList();
    }

    /**
     * Obriši ocenu ako postoji (za rollback)
     */
    public void deleteGradeIfExists(Long studentId, String subjectId, String academicYear) {
        try {
            List<StudentGrade> grades = studentGradeRepository.findByStudentIdAndSubjectId(studentId, subjectId);
            grades.stream()
                .filter(grade -> grade.getAcademicYear().equals(academicYear))
                .forEach(studentGradeRepository::delete);
        } catch (Exception e) {
            // Log greška ali ne prekidaj rollback
            System.err.println("Failed to delete grade during rollback: " + e.getMessage());
        }
    }
}