package rs.ac.uns.acs.nais.ColumnarDatabaseService.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import rs.ac.uns.acs.nais.ColumnarDatabaseService.entity.Subject;
import rs.ac.uns.acs.nais.ColumnarDatabaseService.repository.SubjectRepository;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class SubjectService {

    @Autowired
    private SubjectRepository subjectRepository;

    // === OSNOVNI CRUD ===
    
    public Subject saveSubject(Subject subject) {
        return subjectRepository.save(subject);
    }

    public Optional<Subject> getSubjectById(String subjectId) {
        return subjectRepository.findBySubjectId(subjectId);
    }

    public List<Subject> getAllSubjects() {
        return subjectRepository.findAll();
    }

    public void deleteSubject(String subjectId) {
        subjectRepository.deleteById(subjectId);
    }

    // === SEARCH I FILTER ===
    
    public List<Subject> getSubjectsByDepartment(String department) {
        return subjectRepository.findByDepartment(department);
    }

    public List<Subject> getSubjectsByProfessor(Long professorId) {
        return subjectRepository.findByProfessorId(professorId);
    }

    public List<Subject> getSubjectsByYear(Integer yearOfStudy) {
        return subjectRepository.findByYearOfStudy(yearOfStudy);
    }

    public List<Subject> getSubjectsBySemester(Integer semester) {
        return subjectRepository.findBySemester(semester);
    }

    public List<Subject> getSubjectsByDepartmentAndYear(String department, Integer yearOfStudy) {
        return subjectRepository.findByDepartmentAndYear(department, yearOfStudy);
    }

    public List<Subject> getSubjectsByDepartmentAndSemester(String department, Integer semester) {
        return subjectRepository.findByDepartmentAndSemester(department, semester);
    }

    public List<Subject> getSubjectsByYearAndSemester(Integer yearOfStudy, Integer semester) {
        return subjectRepository.findByYearAndSemester(yearOfStudy, semester);
    }

    public List<Subject> getMandatorySubjects(String department) {
        return subjectRepository.findByDepartmentAndMandatoryStatus(department, true);
    }

    public List<Subject> getElectiveSubjects(String department) {
        return subjectRepository.findByDepartmentAndMandatoryStatus(department, false);
    }

    public List<Subject> getSubjectsByEctsPoints(Integer ectsPoints) {
        return subjectRepository.findByEctsPoints(ectsPoints);
    }

    public List<Subject> getSubjectsByMinEcts(Integer minEcts) {
        return subjectRepository.findByMinEctsPoints(minEcts);
    }

    public List<Subject> searchByName(String subjectName) {
        return subjectRepository.findBySubjectName(subjectName);
    }

    // === STATISTIKE ===
    
    public Long getTotalSubjectsCount() {
        return subjectRepository.count();
    }

    public Long getSubjectsByDepartmentCount(String department) {
        return subjectRepository.countByDepartment(department);
    }

    public Integer getTotalEctsByYear(Integer yearOfStudy) {
        return subjectRepository.getTotalEctsByYear(yearOfStudy);
    }

    public Double getAverageEctsByDepartment(String department) {
        return subjectRepository.getAverageEctsByDepartment(department);
    }

    // === BUSINESS LOGIC ===
    
    public Subject assignProfessor(String subjectId, Long professorId) {
        Optional<Subject> subjectOpt = subjectRepository.findBySubjectId(subjectId);
        if (subjectOpt.isPresent()) {
            Subject subject = subjectOpt.get();
            subject.setProfessorId(professorId);
            return subjectRepository.save(subject);
        }
        throw new RuntimeException("Subject not found with ID: " + subjectId);
    }

    public Subject updateEctsPoints(String subjectId, Integer newEctsPoints) {
        Optional<Subject> subjectOpt = subjectRepository.findBySubjectId(subjectId);
        if (subjectOpt.isPresent()) {
            Subject subject = subjectOpt.get();
            subject.setEctsPoints(newEctsPoints);
            return subjectRepository.save(subject);
        }
        throw new RuntimeException("Subject not found with ID: " + subjectId);
    }

    public Subject updateMandatoryStatus(String subjectId, Boolean isMandatory) {
        Optional<Subject> subjectOpt = subjectRepository.findBySubjectId(subjectId);
        if (subjectOpt.isPresent()) {
            Subject subject = subjectOpt.get();
            subject.setMandatory(isMandatory);
            return subjectRepository.save(subject);
        }
        throw new RuntimeException("Subject not found with ID: " + subjectId);
    }

    public boolean doesSubjectExist(String subjectId) {
        return subjectRepository.findBySubjectId(subjectId).isPresent();
    }

    // === CURRICULUM ANALIZE ===
    
    public CurriculumAnalysis analyzeCurriculum(String department) {
        List<Subject> allSubjects = subjectRepository.findByDepartment(department);
        
        if (allSubjects.isEmpty()) {
            return new CurriculumAnalysis(department, 0, 0, 0, 0.0, 0, 0);
        }

        int totalSubjects = allSubjects.size();
        int mandatoryCount = (int) allSubjects.stream().filter(Subject::isMandatory).count();
        int electiveCount = totalSubjects - mandatoryCount;
        
        double avgEcts = allSubjects.stream()
                .mapToInt(Subject::getEctsPoints)
                .average()
                .orElse(0.0);
        
        int totalEcts = allSubjects.stream()
                .mapToInt(Subject::getEctsPoints)
                .sum();
        
        // Count unique professors
        long uniqueProfessors = allSubjects.stream()
                .map(Subject::getProfessorId)
                .filter(id -> id != null)
                .distinct()
                .count();

        return new CurriculumAnalysis(department, totalSubjects, mandatoryCount, 
                                    electiveCount, avgEcts, totalEcts, (int)uniqueProfessors);
    }

    public Map<Integer, List<Subject>> getSubjectsByYearStructured(String department) {
        List<Subject> subjects = subjectRepository.findByDepartment(department);
        return subjects.stream()
                .collect(Collectors.groupingBy(Subject::getYearOfStudy));
    }

    public Map<Integer, List<Subject>> getSubjectsBySemesterStructured(String department) {
        List<Subject> subjects = subjectRepository.findByDepartment(department);
        return subjects.stream()
                .collect(Collectors.groupingBy(Subject::getSemester));
    }

    public SubjectWorkload calculateProfessorWorkload(Long professorId) {
        List<Subject> professorSubjects = subjectRepository.findByProfessorId(professorId);
        
        if (professorSubjects.isEmpty()) {
            return new SubjectWorkload(professorId, 0, 0, 0.0, "LIGHT");
        }

        int totalSubjects = professorSubjects.size();
        int totalEcts = professorSubjects.stream()
                .mapToInt(Subject::getEctsPoints)
                .sum();
        
        double avgEcts = professorSubjects.stream()
                .mapToInt(Subject::getEctsPoints)
                .average()
                .orElse(0.0);

        String workloadLevel;
        if (totalSubjects >= 6) {
            workloadLevel = "HEAVY";
        } else if (totalSubjects >= 4) {
            workloadLevel = "MODERATE";
        } else {
            workloadLevel = "LIGHT";
        }

        return new SubjectWorkload(professorId, totalSubjects, totalEcts, avgEcts, workloadLevel);
    }

    // === HELPER KLASE ===
    
    public static class CurriculumAnalysis {
        private String department;
        private Integer totalSubjects;
        private Integer mandatorySubjects;
        private Integer electiveSubjects;
        private Double averageEctsPoints;
        private Integer totalEctsPoints;
        private Integer uniqueProfessors;

        public CurriculumAnalysis(String department, Integer totalSubjects, Integer mandatorySubjects,
                                Integer electiveSubjects, Double averageEctsPoints, Integer totalEctsPoints, Integer uniqueProfessors) {
            this.department = department;
            this.totalSubjects = totalSubjects;
            this.mandatorySubjects = mandatorySubjects;
            this.electiveSubjects = electiveSubjects;
            this.averageEctsPoints = averageEctsPoints;
            this.totalEctsPoints = totalEctsPoints;
            this.uniqueProfessors = uniqueProfessors;
        }

        // Getters
        public String getDepartment() { return department; }
        public Integer getTotalSubjects() { return totalSubjects; }
        public Integer getMandatorySubjects() { return mandatorySubjects; }
        public Integer getElectiveSubjects() { return electiveSubjects; }
        public Double getAverageEctsPoints() { return averageEctsPoints; }
        public Integer getTotalEctsPoints() { return totalEctsPoints; }
        public Integer getUniqueProfessors() { return uniqueProfessors; }
    }

    public static class SubjectWorkload {
        private Long professorId;
        private Integer totalSubjects;
        private Integer totalEctsPoints;
        private Double averageEctsPoints;
        private String workloadLevel;

        public SubjectWorkload(Long professorId, Integer totalSubjects, Integer totalEctsPoints, 
                             Double averageEctsPoints, String workloadLevel) {
            this.professorId = professorId;
            this.totalSubjects = totalSubjects;
            this.totalEctsPoints = totalEctsPoints;
            this.averageEctsPoints = averageEctsPoints;
            this.workloadLevel = workloadLevel;
        }

        // Getters
        public Long getProfessorId() { return professorId; }
        public Integer getTotalSubjects() { return totalSubjects; }
        public Integer getTotalEctsPoints() { return totalEctsPoints; }
        public Double getAverageEctsPoints() { return averageEctsPoints; }
        public String getWorkloadLevel() { return workloadLevel; }
    }
}