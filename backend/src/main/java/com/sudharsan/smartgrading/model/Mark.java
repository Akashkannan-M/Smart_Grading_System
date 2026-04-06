package com.sudharsan.smartgrading.model;

import jakarta.persistence.*;

@Entity
@Table(name = "marks")
public class Mark {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "student_id", nullable = false)
    private User student;

    @ManyToOne
    @JoinColumn(name = "subject_id", nullable = false)
    private Subject subject;

    private Integer cia1;
    private Integer cia2;
    private Integer modelExam;
    
    private Integer total;
    private String result;

    public Mark() {}

    @PrePersist
    @PreUpdate
    public void calculateTotalAndResult() {
        int c1 = cia1 != null ? cia1 : 0;
        int c2 = cia2 != null ? cia2 : 0;
        int m = modelExam != null ? modelExam : 0;
        this.total = c1 + c2 + m; 
        this.result = this.total >= 50 ? "PASS" : "FAIL";
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public User getStudent() { return student; }
    public void setStudent(User student) { this.student = student; }
    public Subject getSubject() { return subject; }
    public void setSubject(Subject subject) { this.subject = subject; }
    public Integer getCia1() { return cia1; }
    public void setCia1(Integer cia1) { this.cia1 = cia1; }
    public Integer getCia2() { return cia2; }
    public void setCia2(Integer cia2) { this.cia2 = cia2; }
    public Integer getModelExam() { return modelExam; }
    public void setModelExam(Integer modelExam) { this.modelExam = modelExam; }
    public Integer getTotal() { return total; }
    public void setTotal(Integer total) { this.total = total; }
    public String getResult() { return result; }
    public void setResult(String result) { this.result = result; }
}
