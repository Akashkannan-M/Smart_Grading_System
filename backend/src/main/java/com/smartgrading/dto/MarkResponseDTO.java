package com.smartgrading.dto;

public class MarkResponseDTO {
    private Long id;
    private Long studentId;
    private String studentName;
    private Long subjectId;
    private String subjectName;
    private String examType;
    private Integer marks;
    private Integer maxMarks;
    private Boolean isPass;

    public MarkResponseDTO() {}
    public MarkResponseDTO(Long id, Long studentId, String studentName, Long subjectId, String subjectName, String examType, Integer marks, Integer maxMarks, Boolean isPass) {
        this.id = id;
        this.studentId = studentId;
        this.studentName = studentName;
        this.subjectId = subjectId;
        this.subjectName = subjectName;
        this.examType = examType;
        this.marks = marks;
        this.maxMarks = maxMarks;
        this.isPass = isPass;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Long getStudentId() { return studentId; }
    public void setStudentId(Long studentId) { this.studentId = studentId; }
    public String getStudentName() { return studentName; }
    public void setStudentName(String studentName) { this.studentName = studentName; }
    public Long getSubjectId() { return subjectId; }
    public void setSubjectId(Long subjectId) { this.subjectId = subjectId; }
    public String getSubjectName() { return subjectName; }
    public void setSubjectName(String subjectName) { this.subjectName = subjectName; }
    public String getExamType() { return examType; }
    public void setExamType(String examType) { this.examType = examType; }
    public Integer getMarks() { return marks; }
    public void setMarks(Integer marks) { this.marks = marks; }
    public Integer getMaxMarks() { return maxMarks; }
    public void setMaxMarks(Integer maxMarks) { this.maxMarks = maxMarks; }
    public Boolean getIsPass() { return isPass; }
    public void setIsPass(Boolean isPass) { this.isPass = isPass; }
}
