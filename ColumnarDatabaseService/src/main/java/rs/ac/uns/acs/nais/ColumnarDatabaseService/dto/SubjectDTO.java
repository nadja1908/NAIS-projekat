package rs.ac.uns.acs.nais.ColumnarDatabaseService.dto;

/**
 * DTO za osnovne informacije o predmetu
 */
public class SubjectDTO {
    private String subjectId;
    private String subjectName;
    private String department;
    private String professorName;  // ADD: professorName field
    private Integer yearOfStudy;
    private Integer semester;
    private Integer ectsPoints;
    private Boolean isMandatory;
    private Long professorId;

    // Konstruktori
    public SubjectDTO() {}

    public SubjectDTO(String subjectId, String subjectName, String department, Integer yearOfStudy,
                     Integer semester, Integer ectsPoints, Boolean isMandatory, Long professorId) {
        this.subjectId = subjectId;
        this.subjectName = subjectName;
        this.department = department;
        this.yearOfStudy = yearOfStudy;
        this.semester = semester;
        this.ectsPoints = ectsPoints;
        this.isMandatory = isMandatory;
        this.professorId = professorId;
    }

    // ADD: Extended constructor
    public SubjectDTO(String subjectId, String subjectName, String department, Integer yearOfStudy, Integer semester, Integer ectsPoints, Long professorId, String professorName, Boolean isMandatory) {
        this.subjectId = subjectId;
        this.subjectName = subjectName;
        this.department = department;
        this.yearOfStudy = yearOfStudy;
        this.semester = semester;
        this.ectsPoints = ectsPoints;
        this.professorId = professorId;
        this.professorName = professorName;
        this.isMandatory = isMandatory;
    }

    // Getters i Setters
    public String getSubjectId() { return subjectId; }
    public void setSubjectId(String subjectId) { this.subjectId = subjectId; }


    public String getSubjectName() { return subjectName; }
    public void setSubjectName(String subjectName) { this.subjectName = subjectName; }

    public String getDepartment() { return department; }
    public void setDepartment(String department) { this.department = department; }

    public String getProfessorName() { return professorName; }  // ADD: getter for professorName
    public void setProfessorName(String professorName) { this.professorName = professorName; }

    public Integer getYearOfStudy() { return yearOfStudy; }
    public void setYearOfStudy(Integer yearOfStudy) { this.yearOfStudy = yearOfStudy; }

    public Integer getSemester() { return semester; }
    public void setSemester(Integer semester) { this.semester = semester; }

    public Integer getEctsPoints() { return ectsPoints; }
    public void setEctsPoints(Integer ectsPoints) { this.ectsPoints = ectsPoints; }

    public Boolean getIsMandatory() { return isMandatory; }
    public void setIsMandatory(Boolean isMandatory) { this.isMandatory = isMandatory; }

    public Boolean getMandatory() { return isMandatory; }      // ADD: alias getter for mandatory
    public void setMandatory(Boolean mandatory) { this.isMandatory = mandatory; }

    public Long getProfessorId() { return professorId; }
    public void setProfessorId(Long professorId) { this.professorId = professorId; }
}