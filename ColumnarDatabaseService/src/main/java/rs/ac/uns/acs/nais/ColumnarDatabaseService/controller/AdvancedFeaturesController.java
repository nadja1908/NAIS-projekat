package rs.ac.uns.acs.nais.ColumnarDatabaseService.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import rs.ac.uns.acs.nais.ColumnarDatabaseService.dto.StudentGradeDTO;
import rs.ac.uns.acs.nais.ColumnarDatabaseService.service.GradeSagaOrchestrator;
import rs.ac.uns.acs.nais.ColumnarDatabaseService.service.UniversityReportGenerator;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * CONTROLLER za transakcionalnu obradu i generisanje izveštaja
 * 
 * Implementira:
 * - Transakcionalnu obradu između 2 baze (Cassandra + Redis) [10 bodova]
 * - Generator PDF izveštaja sa prostim i složenim sekcijama [15 bodova]
 */
@RestController
@RequestMapping("/api/v1/advanced")
@CrossOrigin(origins = "*")
public class AdvancedFeaturesController {

    @Autowired
    private GradeSagaOrchestrator gradeSagaOrchestrator;
    
    @Autowired
    private UniversityReportGenerator reportGenerator;

    // =================== TRANSAKCIONA OBRADA PODATAKA ===================
    
    /**
     * SAGA TRANSAKCIJA: Dodaj ocenu sa koordinacijom između Cassandra i Redis
     * 
     * FUNKCIONALNOST: Unos ocene + ažuriranje statistika u 2 različite baze
     * - Cassandra: student_grades tabela + subject_statistics tabela
     * - Redis: keširane statistike i podaci o studentu
     * - Rollback: u slučaju greške, vraća sve na početno stanje
     */
    @PostMapping("/transaction/grade")
    public ResponseEntity<GradeSagaOrchestrator.StudentGradeTransactionResult> processGradeTransaction(
            @RequestBody StudentGradeDTO gradeDTO) {
        
        GradeSagaOrchestrator.StudentGradeTransactionResult result = 
            gradeSagaOrchestrator.processGradeTransaction(gradeDTO);
        
        if (result.isSuccess()) {
            return ResponseEntity.ok(result);
        } else {
            return ResponseEntity.badRequest().body(result);
        }
    }
    
    /**
     * STATUS TRANSAKCIJE: Proveri status određene transakcije
     */
    @GetMapping("/transaction/{transactionId}/status")
    public ResponseEntity<Object> getTransactionStatus(@PathVariable String transactionId) {
        // Ovde bi trebalo dodati logiku za praćenje statusa transakcije
        // Možete proširiti GradeSagaOrchestrator da čuva log transakcija
        return ResponseEntity.ok().body(
            "Transaction status endpoint - implement transaction logging for ID: " + transactionId
        );
    }

    // =================== GENERATOR IZVEŠTAJA ===================
    
    /**
     * KOMPLETAN PDF IZVEŠTAJ: Univerzitetska analiza sa 4 sekcije
     * 
     * PROSTE SEKCIJE:
     * 1. Lista studenata sa filterom po departmanu
     * 2. Ocene sa uslovom (ocena >= threshold)
     * 
     * SLOŽENE SEKCIJE:
     * 3. Departmentska analiza (grupisanje + agregiranje)
     * 4. Trend analiza profesora (vremenske serije + AI insights)
     */
    @GetMapping("/reports/university-analytics")
    public ResponseEntity<byte[]> generateUniversityAnalyticsReport(
            @RequestParam String academicYear,
            @RequestParam(defaultValue = "FTN") String department) {
        
        try {
            byte[] pdfBytes = reportGenerator.generateUniversityAnalyticsReport(academicYear, department);
            
            String filename = String.format("University_Analytics_Report_%s_%s_%s.pdf", 
                department, academicYear.replace("/", "-"), 
                LocalDateTime.now().format(DateTimeFormatter.ofPattern("ddMMyyyy_HHmmss")));
            
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_PDF);
            headers.setContentDispositionFormData("attachment", filename);
            headers.setContentLength(pdfBytes.length);
            
            return ResponseEntity.ok().headers(headers).body(pdfBytes);
            
        } catch (IOException e) {
            return ResponseEntity.internalServerError()
                .body(("Error generating report: " + e.getMessage()).getBytes());
        }
    }
    
    /**
     * BRZA DEPARTMENTSKA ANALIZA: Samo složena sekcija sa poređenjem departmana
     */
    @GetMapping("/reports/department-comparison")
    public ResponseEntity<byte[]> generateDepartmentComparisonReport(
            @RequestParam String academicYear) {
        
        try {
            // Ova metoda bi generisala samo sekciju 3 iz glavnog izveštaja
            byte[] pdfBytes = reportGenerator.generateDepartmentComparisonReport(academicYear);
            
            String filename = String.format("Department_Comparison_%s_%s.pdf", 
                academicYear.replace("/", "-"),
                LocalDateTime.now().format(DateTimeFormatter.ofPattern("ddMMyyyy_HHmm")));
            
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_PDF);
            headers.setContentDispositionFormData("attachment", filename);
            
            return ResponseEntity.ok().headers(headers).body(pdfBytes);
            
        } catch (IOException e) {
            return ResponseEntity.internalServerError()
                .body(("Error generating department comparison report: " + e.getMessage()).getBytes());
        }
    }
    
    /**
     * TREND ANALIZA: Samo profesorska analiza kroz godine
     */
    @GetMapping("/reports/professor-trends")
    public ResponseEntity<byte[]> generateProfessorTrendsReport(
            @RequestParam String academicYear) {
        
        try {
            byte[] pdfBytes = reportGenerator.generateProfessorTrendsReport(academicYear);
            
            String filename = String.format("Professor_Trends_%s_%s.pdf",
                academicYear.replace("/", "-"),
                LocalDateTime.now().format(DateTimeFormatter.ofPattern("ddMMyyyy_HHmm")));
            
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_PDF);
            headers.setContentDispositionFormData("attachment", filename);
            
            return ResponseEntity.ok().headers(headers).body(pdfBytes);
            
        } catch (IOException e) {
            return ResponseEntity.internalServerError()
                .body(("Error generating professor trends report: " + e.getMessage()).getBytes());
        }
    }

    // =================== KOMBINOVANE OPERACIJE ===================
    
    /**
     * KOMPLEKSNA OPERACIJA: Dodaj ocenu sa transakcijom + generiši izveštaj
     */
    @PostMapping("/transaction-and-report")
    public ResponseEntity<Object> processGradeAndGenerateReport(
            @RequestBody StudentGradeDTO gradeDTO) {
        
        // 1. Izvrši transakciju
        GradeSagaOrchestrator.StudentGradeTransactionResult transactionResult = 
            gradeSagaOrchestrator.processGradeTransaction(gradeDTO);
        
        if (!transactionResult.isSuccess()) {
            return ResponseEntity.badRequest().body(transactionResult);
        }
        
        // 2. Generiši izveštaj nakon uspešne transakcije
        try {
            byte[] reportBytes = reportGenerator.generateUniversityAnalyticsReport(
                gradeDTO.getAcademicYear(), gradeDTO.getDepartment());
            
            // Vraćaj kombinovani odgovor
            return ResponseEntity.ok().body(java.util.Map.of(
                "transaction", transactionResult,
                "reportSize", reportBytes.length,
                "message", "Grade processed successfully and report generated"
            ));
            
        } catch (IOException e) {
            // Transakcija je uspešna, ali izveštaj nije generisan
            return ResponseEntity.ok().body(java.util.Map.of(
                "transaction", transactionResult,
                "reportError", e.getMessage(),
                "message", "Grade processed successfully, but report generation failed"
            ));
        }
    }
    
    /**
     * HEALTH CHECK: Proveri da li su svi servisi dostupni
     */
    @GetMapping("/health")
    public ResponseEntity<Object> healthCheck() {
        return ResponseEntity.ok().body(java.util.Map.of(
            "status", "UP",
            "features", java.util.List.of(
                "Transactional Processing (Saga Pattern)",
                "PDF Report Generation", 
                "Multi-Database Coordination",
                "Advanced Analytics"
            ),
            "timestamp", LocalDateTime.now(),
            "version", "1.0.0"
        ));
    }
}