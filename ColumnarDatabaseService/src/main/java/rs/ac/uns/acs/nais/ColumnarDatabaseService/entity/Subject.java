package rs.ac.uns.acs.nais.ColumnarDatabaseService.entity;

import org.springframework.data.cassandra.core.mapping.Column;
import org.springframework.data.cassandra.core.mapping.PrimaryKey;
import org.springframework.data.cassandra.core.mapping.Table;

@Table("subjects")
public class Subject {

    @PrimaryKey("subject_id")
    private String subjectId;

    @Column("subject_name")
    private String subjectName;

    @Column("subject_code")
    private String subjectCode;

    @Column("department")
    private String department;

    @Column("ects_points")
    private Integer ectsPoints;

    @Column("year_of_study")
    private Integer yearOfStudy;

    @Column("semester")
    private Integer semester;

    @Column("professor_id")
    private Long professorId;

    @Column("professor_name")
    private String professorName;

    @Column("is_mandatory")
    private Boolean isMandatory;

    // Constructors
    public Subject() {}

    public Subject(String subjectId, String subjectName, String subjectCode, String department,
                   Integer ectsPoints, Integer yearOfStudy, Integer semester, Long professorId, 
                   String professorName, Boolean isMandatory) {
        this.subjectId = subjectId;
        this.subjectName = subjectName;
        this.subjectCode = subjectCode;
        this.department = department;
        this.ectsPoints = ectsPoints;
        this.yearOfStudy = yearOfStudy;
        this.semester = semester;
        this.professorId = professorId;
        this.professorName = professorName;
        this.isMandatory = isMandatory;
    }

    // Getters and Setters
    public String getSubjectId() {
        return subjectId;
    }

    public void setSubjectId(String subjectId) {
        this.subjectId = subjectId;
    }

    public String getSubjectName() {
        return subjectName;
    }

    public void setSubjectName(String subjectName) {
        this.subjectName = subjectName;
    }

    public String getSubjectCode() {
        return subjectCode;
    }

    public void setSubjectCode(String subjectCode) {
        this.subjectCode = subjectCode;
    }

    public String getDepartment() {
        return department;
    }

    public void setDepartment(String department) {
        this.department = department;
    }

    public Integer getEctsPoints() {
        return ectsPoints;
    }

    public void setEctsPoints(Integer ectsPoints) {
        this.ectsPoints = ectsPoints;
    }

    public Integer getYearOfStudy() {
        return yearOfStudy;
    }

    public void setYearOfStudy(Integer yearOfStudy) {
        this.yearOfStudy = yearOfStudy;
    }

    public Integer getSemester() {
        return semester;
    }

    public void setSemester(Integer semester) {
        this.semester = semester;
    }

    public Long getProfessorId() {
        return professorId;
    }

    public void setProfessorId(Long professorId) {
        this.professorId = professorId;
    }

    public String getProfessorName() {
        return professorName;
    }

    public void setProfessorName(String professorName) {
        this.professorName = professorName;
    }

    public Boolean isMandatory() {
        return isMandatory;
    }

    public void setMandatory(Boolean isMandatory) {
        this.isMandatory = isMandatory;
    }
}