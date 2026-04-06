package com.sudharsan.smartgrading.dto;

public class MarkDto {
    private Long studentId;
    private Long subjectId;
    private Integer cia1;
    private Integer cia2;
    private Integer modelExam;

    public MarkDto() {}

    public Long getStudentId() { return studentId; }
    public void setStudentId(Long studentId) { this.studentId = studentId; }
    public Long getSubjectId() { return subjectId; }
    public void setSubjectId(Long subjectId) { this.subjectId = subjectId; }
    public Integer getCia1() { return cia1; }
    public void setCia1(Integer cia1) { this.cia1 = cia1; }
    public Integer getCia2() { return cia2; }
    public void setCia2(Integer cia2) { this.cia2 = cia2; }
    public Integer getModelExam() { return modelExam; }
    public void setModelExam(Integer modelExam) { this.modelExam = modelExam; }
}
