package rs.ac.uns.acs.nais.ColumnarDatabaseService.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import rs.ac.uns.acs.nais.ColumnarDatabaseService.dto.StudentGradeDTO;
import rs.ac.uns.acs.nais.ColumnarDatabaseService.entity.StudentGrade;
import rs.ac.uns.acs.nais.ColumnarDatabaseService.entity.SubjectStatistics;

/**
 * SAGA ORCHESTRATOR za transakcionalnu obradu između Cassandra i Redis
 * 
 * FUNKCIJONALNOST: Upis ocene studenta sa ažuriranjem statistika
 * - Cassandra: nova ocena u student_grades
 * - Redis: keširane statistike predmeta 
 * - Rollback ako bilo koja operacija ne uspe
 */
@Service
public class GradeSagaOrchestrator {

    @Autowired
    private StudentGradeService studentGradeService;
    
    @Autowired
    private SubjectStatisticsService subjectStatisticsService;
    
    @Autowired
    private RedisService redisService;
    
    @Autowired
    private StudentGradeExtensionService extensionService;

    /**
     * TRANSAKCIONA OBRADA: Dodaj ocenu + ažuriraj statistike
     */
    public StudentGradeTransactionResult processGradeTransaction(StudentGradeDTO gradeDTO) {
        String transactionId = java.util.UUID.randomUUID().toString();

        // VALIDATION: subjectId must not be null or empty
        if (gradeDTO.getSubjectId() == null || gradeDTO.getSubjectId().trim().isEmpty()) {
            return new StudentGradeTransactionResult(transactionId, false,
                "Validation error: subjectId must not be null or empty", null, null);
        }

        try {
            // KORAK 1: Sačuvaj ocenu u Cassandra
            StudentGrade savedGrade = studentGradeService.saveGrade(convertToEntity(gradeDTO));

            // KORAK 2: Ažuriraj statistike predmeta u Cassandra (void method)
            subjectStatisticsService.updateStatisticsAfterGrade(
                gradeDTO.getSubjectId(), gradeDTO.getDepartment(), gradeDTO.getAcademicYear());

            // KORAK 3: Ažuriraj keš u Redis (use up-to-date stats)
            subjectStatisticsService.updateStatisticsAfterGrade(
                gradeDTO.getSubjectId(), gradeDTO.getDepartment(), gradeDTO.getAcademicYear());

            SubjectStatistics updatedStats = subjectStatisticsService.getStatistics(
                gradeDTO.getSubjectId(), gradeDTO.getDepartment(), gradeDTO.getAcademicYear());

            if (updatedStats != null) {
                redisService.updateSubjectStatisticsCache(updatedStats);
            }

            // KORAK 4: Ažuriraj student keš u Redis
            redisService.updateStudentGradesCache(gradeDTO.getStudentId(), savedGrade);

            return new StudentGradeTransactionResult(transactionId, true,
                "Transaction completed successfully", savedGrade, updatedStats);

        } catch (Exception e) {
            // SAGA ROLLBACK
            return rollbackGradeTransaction(transactionId, gradeDTO, e);
        }
    }
    
    /**
     * ROLLBACK funkcionalnost
     */
    private StudentGradeTransactionResult rollbackGradeTransaction(String transactionId, 
            StudentGradeDTO gradeDTO, Exception error) {
        
        try {
            // Rollback Cassandra - obriši gradu ako je dodana
            extensionService.deleteGradeIfExists(gradeDTO.getStudentId(), 
                gradeDTO.getSubjectId(), gradeDTO.getAcademicYear());
            
            // Rollback Redis - obriši iz keša
            redisService.invalidateCache("student:" + gradeDTO.getStudentId());
            redisService.invalidateCache("subject:" + gradeDTO.getSubjectId());
            
            return new StudentGradeTransactionResult(transactionId, false, 
                "Transaction rolled back due to: " + error.getMessage(), null, null);
            
        } catch (Exception rollbackError) {
            return new StudentGradeTransactionResult(transactionId, false, 
                "CRITICAL: Rollback failed! Manual intervention required: " + rollbackError.getMessage(), 
                null, null);
        }
    }
    
    /**
     * DTO konverzija
     */
    private StudentGrade convertToEntity(StudentGradeDTO dto) {
        StudentGrade grade = new StudentGrade();
        grade.setStudentId(dto.getStudentId());
        grade.setSubjectId(dto.getSubjectId());
        grade.setAcademicYear(dto.getAcademicYear());
        grade.setExamDate(dto.getExamDate().atStartOfDay());
        grade.setGrade(dto.getGrade());
        grade.setExamType(dto.getExamType());
        grade.setProfessorId(dto.getProfessorId());
        grade.setDepartment(dto.getDepartment());
        return grade;
    }
    
    /**
     * Result klasa za transakciju
     */
    public static class StudentGradeTransactionResult {
        private String transactionId;
        private boolean success;
        private String message;
        private StudentGrade grade;
        private SubjectStatistics statistics;
        
        public StudentGradeTransactionResult(String transactionId, boolean success, String message, 
                StudentGrade grade, SubjectStatistics statistics) {
            this.transactionId = transactionId;
            this.success = success;
            this.message = message;
            this.grade = grade;
            this.statistics = statistics;
        }
        
        // Getters
        public String getTransactionId() { return transactionId; }
        public boolean isSuccess() { return success; }
        public String getMessage() { return message; }
        public StudentGrade getGrade() { return grade; }
        public SubjectStatistics getStatistics() { return statistics; }
    }
}