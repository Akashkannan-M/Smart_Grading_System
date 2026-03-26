package com.smartgrading.dto;

import com.smartgrading.entity.Mark;

public class MarkDTO {
    private Long id;
    private Long studentId;
    private Long subjectId;
    private Mark.ExamType examType;
    private Integer marks;

    public MarkDTO() {}
    public MarkDTO(Long id, Long studentId, Long subjectId, Mark.ExamType examType, Integer marks) {
        this.id = id;
        this.studentId = studentId;
        this.subjectId = subjectId;
        this.examType = examType;
        this.marks = marks;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Long getStudentId() { return studentId; }
    public void setStudentId(Long studentId) { this.studentId = studentId; }
    public Long getSubjectId() { return subjectId; }
    public void setSubjectId(Long subjectId) { this.subjectId = subjectId; }
    public Mark.ExamType getExamType() { return examType; }
    public void setExamType(Mark.ExamType examType) { this.examType = examType; }
    public Integer getMarks() { return marks; }
    public void setMarks(Integer marks) { this.marks = marks; }
}
