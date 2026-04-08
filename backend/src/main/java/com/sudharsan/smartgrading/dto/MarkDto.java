package com.sudharsan.smartgrading.dto;

public class MarkDto {
    private Long studentId;
    private Long subjectId;
    private String cia1;
    private String cia2;
    private String modelExam;

    public MarkDto() {}

    public Long getStudentId() { return studentId; }
    public void setStudentId(Long studentId) { this.studentId = studentId; }
    public Long getSubjectId() { return subjectId; }
    public void setSubjectId(Long subjectId) { this.subjectId = subjectId; }
    public String getCia1() { return cia1; }
    public void setCia1(String cia1) { this.cia1 = cia1; }
    public String getCia2() { return cia2; }
    public void setCia2(String cia2) { this.cia2 = cia2; }
    public String getModelExam() { return modelExam; }
    public void setModelExam(String modelExam) { this.modelExam = modelExam; }
}
