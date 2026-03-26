package com.smartgrading.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "marks")
public class Mark {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "student_id")
    private User student;

    @ManyToOne
    @JoinColumn(name = "subject_id")
    private Subject subject;

    @Column(name = "exam_type")
    @Enumerated(EnumType.STRING)
    private ExamType examType;

    private Integer marks;

    public Mark() {}
    public Mark(Long id, User student, Subject subject, ExamType examType, Integer marks) {
        this.id = id;
        this.student = student;
        this.subject = subject;
        this.examType = examType;
        this.marks = marks;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public User getStudent() { return student; }
    public void setStudent(User student) { this.student = student; }
    public Subject getSubject() { return subject; }
    public void setSubject(Subject subject) { this.subject = subject; }
    public ExamType getExamType() { return examType; }
    public void setExamType(ExamType examType) { this.examType = examType; }
    public Integer getMarks() { return marks; }
    public void setMarks(Integer marks) { this.marks = marks; }

    public enum ExamType {
        CIA1, CIA2, MODEL
    }
}
