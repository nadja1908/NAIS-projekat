package rs.ac.uns.acs.nais.ColumnarDatabaseService.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import rs.ac.uns.acs.nais.ColumnarDatabaseService.entity.Student;
import rs.ac.uns.acs.nais.ColumnarDatabaseService.repository.StudentRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
public class StudentService {

    @Autowired
    private StudentRepository studentRepository;

    // === OSNOVNI CRUD ===
    
    public Student saveStudent(Student student) {
        return studentRepository.save(student);
    }

    public Optional<Student> getStudentById(Long studentId) {
        return studentRepository.findByStudentId(studentId);
    }

    public Optional<Student> getStudentByIndex(String indexNumber) {
        return studentRepository.findByIndexNumber(indexNumber);
    }

    public List<Student> getAllStudents() {
        return studentRepository.findAll();
    }

    public void deleteStudent(Long studentId) {
        studentRepository.deleteById(studentId);
    }

    // === SEARCH I FILTER ===
    
    public List<Student> getActiveStudents() {
        return studentRepository.findActiveStudents();
    }

    public List<Student> getStudentsByProgram(String studyProgram) {
        return studentRepository.findByStudyProgram(studyProgram);
    }

    public List<Student> getStudentsByYear(Integer currentYear) {
        return studentRepository.findByCurrentYear(currentYear);
    }

    public List<Student> getStudentsByProgramAndYear(String studyProgram, Integer year) {
        return studentRepository.findByStudyProgramAndYear(studyProgram, year);
    }

    public List<Student> getStudentsByEnrollmentPeriod(LocalDate startDate, LocalDate endDate) {
        return studentRepository.findByEnrollmentDateBetween(startDate, endDate);
    }

    public List<Student> searchByFirstName(String firstName) {
        return studentRepository.findByFirstName(firstName);
    }

    public List<Student> searchByLastName(String lastName) {
        return studentRepository.findByLastName(lastName);
    }

    // === STATISTIKE ===
    
    public Long getTotalStudentsCount() {
        return studentRepository.count();
    }

    public Long getActiveStudentsCount() {
        return studentRepository.countActiveStudents();
    }

    public Long getStudentsByProgramCount(String studyProgram) {
        return studentRepository.countByStudyProgram(studyProgram);
    }

    // === BUSINESS LOGIC ===
    
    public Student activateStudent(Long studentId) {
        Optional<Student> studentOpt = studentRepository.findByStudentId(studentId);
        if (studentOpt.isPresent()) {
            Student student = studentOpt.get();
            student.setActive(true);
            return studentRepository.save(student);
        }
        throw new RuntimeException("Student not found with ID: " + studentId);
    }

    public Student deactivateStudent(Long studentId) {
        Optional<Student> studentOpt = studentRepository.findByStudentId(studentId);
        if (studentOpt.isPresent()) {
            Student student = studentOpt.get();
            student.setActive(false);
            return studentRepository.save(student);
        }
        throw new RuntimeException("Student not found with ID: " + studentId);
    }

    public Student updateStudentYear(Long studentId, Integer newYear) {
        Optional<Student> studentOpt = studentRepository.findByStudentId(studentId);
        if (studentOpt.isPresent()) {
            Student student = studentOpt.get();
            student.setCurrentYearOfStudy(newYear);
            return studentRepository.save(student);
        }
        throw new RuntimeException("Student not found with ID: " + studentId);
    }

    public boolean doesStudentExist(Long studentId) {
        return studentRepository.findByStudentId(studentId).isPresent();
    }

    public boolean isIndexNumberTaken(String indexNumber) {
        return studentRepository.findByIndexNumber(indexNumber).isPresent();
    }

    // === ANALITIČKI UPITI ===
    
    public StudentSummary getStudentSummary(String studyProgram) {
        List<Student> students = studentRepository.findByStudyProgram(studyProgram);
        
        long activeCount = students.stream().filter(Student::isActive).count();
        long inactiveCount = students.size() - activeCount;
        
        // Count by years
        long firstYear = students.stream().filter(s -> s.getCurrentYearOfStudy() == 1).count();
        long secondYear = students.stream().filter(s -> s.getCurrentYearOfStudy() == 2).count();
        long thirdYear = students.stream().filter(s -> s.getCurrentYearOfStudy() == 3).count();
        long fourthYear = students.stream().filter(s -> s.getCurrentYearOfStudy() == 4).count();
        
        return new StudentSummary(studyProgram, students.size(), (int)activeCount, (int)inactiveCount,
                                (int)firstYear, (int)secondYear, (int)thirdYear, (int)fourthYear);
    }

    // === HELPER KLASA ===
    
    public static class StudentSummary {
        private String studyProgram;
        private Integer totalStudents;
        private Integer activeStudents;
        private Integer inactiveStudents;
        private Integer firstYearCount;
        private Integer secondYearCount;
        private Integer thirdYearCount;
        private Integer fourthYearCount;

        public StudentSummary(String studyProgram, Integer totalStudents, Integer activeStudents, 
                            Integer inactiveStudents, Integer firstYearCount, Integer secondYearCount,
                            Integer thirdYearCount, Integer fourthYearCount) {
            this.studyProgram = studyProgram;
            this.totalStudents = totalStudents;
            this.activeStudents = activeStudents;
            this.inactiveStudents = inactiveStudents;
            this.firstYearCount = firstYearCount;
            this.secondYearCount = secondYearCount;
            this.thirdYearCount = thirdYearCount;
            this.fourthYearCount = fourthYearCount;
        }

        // Getters
        public String getStudyProgram() { return studyProgram; }
        public Integer getTotalStudents() { return totalStudents; }
        public Integer getActiveStudents() { return activeStudents; }
        public Integer getInactiveStudents() { return inactiveStudents; }
        public Integer getFirstYearCount() { return firstYearCount; }
        public Integer getSecondYearCount() { return secondYearCount; }
        public Integer getThirdYearCount() { return thirdYearCount; }
        public Integer getFourthYearCount() { return fourthYearCount; }
    }
}