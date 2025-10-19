package rs.ac.uns.acs.nais.ColumnarDatabaseService.service;

import com.itextpdf.text.*;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import rs.ac.uns.acs.nais.ColumnarDatabaseService.dto.AnalyticsResponseDTO;
import rs.ac.uns.acs.nais.ColumnarDatabaseService.entity.Student;
import rs.ac.uns.acs.nais.ColumnarDatabaseService.entity.StudentGrade;
import rs.ac.uns.acs.nais.ColumnarDatabaseService.entity.SubjectStatistics;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;

/**
 * GENERATOR IZVEŠTAJA u PDF formatu
 * 
 * PROSTE SEKCIJE (6 bodova):
 * 1. Lista svih studenata sa filterom
 * 2. Ocene po predmetu sa uslovom
 * 
 * SLOŽENE SEKCIJE (9 bodova):  
 * 3. Departmentska analiza sa grupisanjem i agregiranjem
 * 4. Trend analiza profesora kroz godine
 */
@Service
public class UniversityReportGenerator {

    @Autowired
    private StudentService studentService;
    
    @Autowired
    private StudentGradeService studentGradeService;
    
    @Autowired
    private SubjectStatisticsService subjectStatisticsService;
    
    @Autowired
    private ProfessorPerformanceService professorPerformanceService;
    
    @Autowired
    private StudentGradeExtensionService extensionService;

    /**
     * GLAVNI IZVEŠTAJ: Univerzitetska analiza uspeha studenata
     */
    public byte[] generateUniversityAnalyticsReport(String academicYear, String department) throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        Document document = new Document(PageSize.A4);
        
        try {
            PdfWriter.getInstance(document, baos);
            document.open();
            
            // NASLOV IZVEŠTAJA
            addReportTitle(document, "UNIVERSITY STUDENT SUCCESS ANALYSIS REPORT", academicYear, department);
            
            // PROSTA SEKCIJA 1: Lista aktivnih studenata
            addSimpleSection1_StudentList(document, department);
            
            // PROSTA SEKCIJA 2: Ocene sa filterom po oceni
            addSimpleSection2_GradesByThreshold(document, academicYear, 8.0);
            
            // SLOŽENA SEKCIJA 1: Departmentska analiza (agregiranje + grupisanje)
            addComplexSection1_DepartmentAnalysis(document, academicYear);
            
            // SLOŽENA SEKCIJA 2: Trend analiza profesora (vremenske serije)
            addComplexSection2_ProfessorTrendAnalysis(document, academicYear);
            
            // FOOTER
            addReportFooter(document);
            
        } catch (DocumentException e) {
            throw new IOException("Error generating PDF report", e);
        } finally {
            document.close();
        }
        
        return baos.toByteArray();
    }
    
    /**
     * PROSTA SEKCIJA 1: Lista studenata sa filterom po departmanu
     */
    private void addSimpleSection1_StudentList(Document document, String department) throws DocumentException {
        // Naslov sekcije
        Font sectionFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 16, BaseColor.DARK_GRAY);
        Paragraph sectionTitle = new Paragraph("1. ACTIVE STUDENTS BY DEPARTMENT", sectionFont);
        sectionTitle.setSpacingBefore(20);
        sectionTitle.setSpacingAfter(10);
        document.add(sectionTitle);
        
        // Filter objaašnjenje
        Font descFont = FontFactory.getFont(FontFactory.HELVETICA, 10, BaseColor.GRAY);
        Paragraph desc = new Paragraph("Filter: Active students in " + department + " department", descFont);
        document.add(desc);
        
        // Tabela sa studentima
        PdfPTable table = new PdfPTable(5);
        table.setWidthPercentage(100);
        table.setSpacingBefore(10);
        
        // Header
        Font headerFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 10, BaseColor.WHITE);
        addTableHeader(table, headerFont, BaseColor.BLUE, 
            "Student ID", "Name", "Index", "Year", "Program");
        
        // Podaci
        List<Student> students = extensionService.getActiveStudentsByDepartment(department);
        Font dataFont = FontFactory.getFont(FontFactory.HELVETICA, 9);
        
        for (Student student : students) {
            table.addCell(new PdfPCell(new Phrase(student.getStudentId().toString(), dataFont)));
            table.addCell(new PdfPCell(new Phrase(student.getFirstName() + " " + student.getLastName(), dataFont)));
            table.addCell(new PdfPCell(new Phrase(student.getIndexNumber(), dataFont)));
            table.addCell(new PdfPCell(new Phrase(student.getCurrentYearOfStudy().toString(), dataFont)));
            table.addCell(new PdfPCell(new Phrase(student.getStudyProgram(), dataFont)));
        }
        
        document.add(table);
        
        // Statistika
        Paragraph stats = new Paragraph("Total active students: " + students.size(), descFont);
        stats.setSpacingBefore(5);
        document.add(stats);
    }
    
    /**
     * PROSTA SEKCIJA 2: Ocene sa uslovom (ocena >= threshold)
     */
    private void addSimpleSection2_GradesByThreshold(Document document, String academicYear, Double threshold) throws DocumentException {
        Font sectionFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 16, BaseColor.DARK_GRAY);
        Paragraph sectionTitle = new Paragraph("2. HIGH PERFORMANCE GRADES", sectionFont);
        sectionTitle.setSpacingBefore(20);
        sectionTitle.setSpacingAfter(10);
        document.add(sectionTitle);
        
        Font descFont = FontFactory.getFont(FontFactory.HELVETICA, 10, BaseColor.GRAY);
        Paragraph desc = new Paragraph("Filter: Grades >= " + threshold + " in academic year " + academicYear, descFont);
        document.add(desc);
        
        // Tabela
        PdfPTable table = new PdfPTable(6);
        table.setWidthPercentage(100);
        table.setSpacingBefore(10);
        
        Font headerFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 10, BaseColor.WHITE);
        addTableHeader(table, headerFont, BaseColor.GREEN,
            "Student ID", "Subject", "Grade", "Exam Type", "Date", "Professor");
        
        // Podataci - poziv servisa koji filtrira ocene
        List<StudentGrade> highGrades = extensionService.getGradesByThresholdAndYear(threshold, academicYear);
        Font dataFont = FontFactory.getFont(FontFactory.HELVETICA, 9);
        
        for (StudentGrade grade : highGrades) {
            table.addCell(new PdfPCell(new Phrase(grade.getStudentId().toString(), dataFont)));
            table.addCell(new PdfPCell(new Phrase(grade.getSubjectId(), dataFont)));
            table.addCell(new PdfPCell(new Phrase(String.format("%.1f", grade.getGrade()), dataFont)));
            table.addCell(new PdfPCell(new Phrase(grade.getExamType(), dataFont)));
            table.addCell(new PdfPCell(new Phrase(grade.getExamDate().toLocalDate().toString(), dataFont)));
            table.addCell(new PdfPCell(new Phrase(grade.getProfessorName(), dataFont)));
        }
        
        document.add(table);
        
        // Analitika
        double avgGrade = highGrades.stream().mapToDouble(StudentGrade::getGrade).average().orElse(0.0);
        Paragraph analytics = new Paragraph(
            String.format("Total high-performance exams: %d | Average grade: %.2f", highGrades.size(), avgGrade), 
            descFont);
        analytics.setSpacingBefore(5);
        document.add(analytics);
    }
    
    /**
     * SLOŽENA SEKCIJA 1: Departmentska analiza (grupisanje + agregiranje)
     */
    private void addComplexSection1_DepartmentAnalysis(Document document, String academicYear) throws DocumentException {
        Font sectionFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 16, BaseColor.DARK_GRAY);
        Paragraph sectionTitle = new Paragraph("3. DEPARTMENT COMPARATIVE ANALYSIS", sectionFont);
        sectionTitle.setSpacingBefore(25);
        sectionTitle.setSpacingAfter(10);
        document.add(sectionTitle);
        
        Font descFont = FontFactory.getFont(FontFactory.HELVETICA, 10, BaseColor.GRAY);
        Paragraph desc = new Paragraph("Complex aggregation: Grouping by department with statistical analysis", descFont);
        document.add(desc);
        
        // Tabela sa departmentskim poređenjem
        PdfPTable table = new PdfPTable(6);
        table.setWidthPercentage(100);
        table.setSpacingBefore(10);
        
        Font headerFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 10, BaseColor.WHITE);
        addTableHeader(table, headerFont, BaseColor.RED,
            "Department", "Avg Grade", "Pass Rate %", "Total Students", "Total Subjects", "Performance Rank");
        
        // Podataci iz servisa koji radi grupisanje i agregiranje
        List<SubjectStatisticsService.DepartmentComparison> comparisons = 
            subjectStatisticsService.compareDepartments(academicYear);
        
        Font dataFont = FontFactory.getFont(FontFactory.HELVETICA, 9);
        
        for (SubjectStatisticsService.DepartmentComparison comp : comparisons) {
            table.addCell(new PdfPCell(new Phrase(comp.getDepartment(), dataFont)));
            table.addCell(new PdfPCell(new Phrase(String.format("%.2f", comp.getAverageGrade()), dataFont)));
            table.addCell(new PdfPCell(new Phrase(String.format("%.1f%%", comp.getAveragePassRate()), dataFont)));
            table.addCell(new PdfPCell(new Phrase(comp.getTotalStudents().toString(), dataFont)));
            table.addCell(new PdfPCell(new Phrase(comp.getTotalSubjects().toString(), dataFont)));
            
            // Colorized performance rank
            PdfPCell rankCell = new PdfPCell(new Phrase(determineRank(comp.getAveragePassRate()), dataFont));
            rankCell.setBackgroundColor(getRankColor(comp.getAveragePassRate()));
            table.addCell(rankCell);
        }
        
        document.add(table);
        
        // Kompleksna analitika
        Paragraph analysis = new Paragraph("ANALYSIS SUMMARY:", 
            FontFactory.getFont(FontFactory.HELVETICA_BOLD, 11));
        analysis.setSpacingBefore(10);
        document.add(analysis);
        
        if (!comparisons.isEmpty()) {
            SubjectStatisticsService.DepartmentComparison best = comparisons.stream()
                .max((a, b) -> Double.compare(a.getAveragePassRate(), b.getAveragePassRate()))
                .orElse(null);
            
            SubjectStatisticsService.DepartmentComparison worst = comparisons.stream()
                .min((a, b) -> Double.compare(a.getAveragePassRate(), b.getAveragePassRate()))
                .orElse(null);
            
            if (best != null && worst != null) {
                Paragraph insights = new Paragraph(
                    String.format("• Best performing department: %s (%.1f%% pass rate)\n" +
                                 "• Department needing attention: %s (%.1f%% pass rate)\n" +
                                 "• Performance gap: %.1f percentage points",
                        best.getDepartment(), best.getAveragePassRate(),
                        worst.getDepartment(), worst.getAveragePassRate(),
                        best.getAveragePassRate() - worst.getAveragePassRate()),
                    FontFactory.getFont(FontFactory.HELVETICA, 10));
                document.add(insights);
            }
        }
    }
    
    /**
     * SLOŽENA SEKCIJA 2: Trend analiza profesora kroz godine
     */
    private void addComplexSection2_ProfessorTrendAnalysis(Document document, String academicYear) throws DocumentException {
        Font sectionFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 16, BaseColor.DARK_GRAY);
        Paragraph sectionTitle = new Paragraph("4. PROFESSOR PERFORMANCE TREND ANALYSIS", sectionFont);
        sectionTitle.setSpacingBefore(25);
        sectionTitle.setSpacingAfter(10);
        document.add(sectionTitle);
        
        Font descFont = FontFactory.getFont(FontFactory.HELVETICA, 10, BaseColor.GRAY);
        Paragraph desc = new Paragraph("Complex temporal analysis: Multi-year trend calculations with AI insights", descFont);
        document.add(desc);
        
        // Analiza trendova profesora
        List<ProfessorPerformanceService.ProfessorTrendAnalysis> trends = 
            professorPerformanceService.analyzeProfessorTrends(academicYear);
        
        // Tabela
        PdfPTable table = new PdfPTable(5);
        table.setWidthPercentage(100);
        table.setSpacingBefore(10);
        
        Font headerFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 10, BaseColor.WHITE);
        addTableHeader(table, headerFont, BaseColor.ORANGE,
            "Professor ID", "Trend Direction", "Pass Rate Change", "Grade Change", "AI Insight");
        
        Font dataFont = FontFactory.getFont(FontFactory.HELVETICA, 9);
        
        for (ProfessorPerformanceService.ProfessorTrendAnalysis trend : trends) {
            table.addCell(new PdfPCell(new Phrase(trend.getProfessorId(), dataFont))); // Already String now
            
            // Trend direction sa bojom
            PdfPCell trendCell = new PdfPCell(new Phrase(trend.getTrendDirection(), dataFont));
            trendCell.setBackgroundColor(getTrendColor(trend.getTrendDirection()));
            table.addCell(trendCell);
            
            table.addCell(new PdfPCell(new Phrase(String.format("%.1f%%", trend.getPassRateChange()), dataFont)));
            table.addCell(new PdfPCell(new Phrase(String.format("%.2f", trend.getGradeChange()), dataFont)));
            table.addCell(new PdfPCell(new Phrase(trend.getInsight(), dataFont))); // FIX: getAiInsight() -> getInsight()
        }
        
        document.add(table);
        
        // Trend summary
        long improvingCount = trends.stream().filter(t -> "IMPROVING".equals(t.getTrendDirection())).count();
        long decliningCount = trends.stream().filter(t -> "DECLINING".equals(t.getTrendDirection())).count();
        
        Paragraph trendSummary = new Paragraph(
            String.format("TREND OVERVIEW: %d professors improving, %d professors declining, %d stable",
                improvingCount, decliningCount, trends.size() - improvingCount - decliningCount),
            FontFactory.getFont(FontFactory.HELVETICA_BOLD, 11));
        trendSummary.setSpacingBefore(10);
        document.add(trendSummary);
    }
    
    /**
     * Generiši samo departmentsko poređenje
     */
    public byte[] generateDepartmentComparisonReport(String academicYear) throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        Document document = new Document(PageSize.A4);
        
        try {
            PdfWriter.getInstance(document, baos);
            document.open();
            
            addReportTitle(document, "DEPARTMENT COMPARISON REPORT", academicYear, "ALL");
            addComplexSection1_DepartmentAnalysis(document, academicYear);
            addReportFooter(document);
            
        } catch (DocumentException e) {
            throw new IOException("Error generating PDF report", e);
        } finally {
            document.close();
        }
        
        return baos.toByteArray();
    }

    /**
     * Generiši samo trend analizu profesora
     */
    public byte[] generateProfessorTrendsReport(String academicYear) throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        Document document = new Document(PageSize.A4);
        
        try {
            PdfWriter.getInstance(document, baos);
            document.open();
            
            addReportTitle(document, "PROFESSOR TRENDS ANALYSIS", academicYear, "ALL");
            addComplexSection2_ProfessorTrendAnalysis(document, academicYear);
            addReportFooter(document);
            
        } catch (DocumentException e) {
            throw new IOException("Error generating PDF report", e);
        } finally {
            document.close();
        }
        
        return baos.toByteArray();
    }
    private void addReportTitle(Document document, String title, String year, String dept) throws DocumentException {
        Font titleFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 20, BaseColor.DARK_GRAY);
        Paragraph titlePara = new Paragraph(title, titleFont);
        titlePara.setAlignment(Element.ALIGN_CENTER);
        titlePara.setSpacingAfter(10);
        document.add(titlePara);
        
        Font subtitleFont = FontFactory.getFont(FontFactory.HELVETICA, 14, BaseColor.GRAY);
        Paragraph subtitle = new Paragraph("Academic Year: " + year + " | Department: " + dept, subtitleFont);
        subtitle.setAlignment(Element.ALIGN_CENTER);
        subtitle.setSpacingAfter(20);
        document.add(subtitle);
        
        // Datum generisanja
        String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm"));
        Paragraph datePara = new Paragraph("Generated on: " + timestamp, 
            FontFactory.getFont(FontFactory.HELVETICA, 10, BaseColor.LIGHT_GRAY));
        datePara.setAlignment(Element.ALIGN_RIGHT);
        datePara.setSpacingAfter(15);
        document.add(datePara);
    }
    
    private void addTableHeader(PdfPTable table, Font headerFont, BaseColor color, String... headers) {
        for (String header : headers) {
            PdfPCell cell = new PdfPCell(new Phrase(header, headerFont));
            cell.setBackgroundColor(color);
            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
            cell.setPadding(8);
            table.addCell(cell);
        }
    }
    
    private void addReportFooter(Document document) throws DocumentException {
        Paragraph footer = new Paragraph(
            "Report generated by NAIS University Analytics System | Team IN_G1_TIM6",
            FontFactory.getFont(FontFactory.HELVETICA, 8, BaseColor.LIGHT_GRAY));
        footer.setAlignment(Element.ALIGN_CENTER);
        footer.setSpacingBefore(30);
        document.add(footer);
    }
    
    private String determineRank(Double passRate) {
        if (passRate >= 85.0) return "EXCELLENT";
        else if (passRate >= 70.0) return "GOOD";
        else if (passRate >= 60.0) return "AVERAGE";
        else return "NEEDS_ATTENTION";
    }
    
    private BaseColor getRankColor(Double passRate) {
        if (passRate >= 85.0) return BaseColor.GREEN;
        else if (passRate >= 70.0) return BaseColor.YELLOW;
        else if (passRate >= 60.0) return BaseColor.ORANGE;
        else return BaseColor.RED;
    }
    
    private BaseColor getTrendColor(String trend) {
        switch (trend) {
            case "IMPROVING": return BaseColor.GREEN;
            case "DECLINING": return BaseColor.RED;
            default: return BaseColor.GRAY;
        }
    }
}