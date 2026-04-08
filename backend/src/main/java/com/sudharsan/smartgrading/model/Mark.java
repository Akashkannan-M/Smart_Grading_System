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

    private String cia1;
    private String cia2;
    private String modelExam;
    
    private String cia1Result;
    private String cia2Result;
    private String modelResult;
    
    private Integer total;
    private String result;

    public Mark() {}

    @PrePersist
    @PreUpdate
    public void calculateTotalAndResult() {
        this.cia1Result = calculateIndividualResult(cia1, 30);
        this.cia2Result = calculateIndividualResult(cia2, 30);
        this.modelResult = calculateIndividualResult(modelExam, 50);

        int c1 = parseMark(cia1);
        int c2 = parseMark(cia2);
        int m = parseMark(modelExam);
        this.total = c1 + c2 + m; 

        // Refined Result Logic (Requirement 5)
        boolean c1f = "FAIL".equals(cia1Result);
        boolean c2f = "FAIL".equals(cia2Result);
        boolean mf = "FAIL".equals(modelResult);
        
        boolean c1a = "ABSENT".equals(cia1Result);
        boolean c2a = "ABSENT".equals(cia2Result);
        boolean ma = "ABSENT".equals(modelResult);
        
        boolean c1m = "".equals(cia1Result);
        boolean c2m = "".equals(cia2Result);
        boolean mm = "".equals(modelResult);

        boolean anyFail = c1f || c2f || mf;
        
        if (anyFail) {
            this.result = "FAIL";
        } else if (c1m && c2m && mm) {
            this.result = ""; // No marks at all
        } else {
            // Check if all actually entered marks are AB
            boolean hasAtLeastOneNumeric = (!c1m && !c1a) || (!c2m && !c2a) || (!mm && !ma);
            if (hasAtLeastOneNumeric) {
                this.result = "PASS";
            } else {
                // All entered marks are AB (some might be -)
                this.result = "ABSENT";
            }
        }
    }

    private String calculateIndividualResult(String val, int passMark) {
        if (val == null || val.equals("-") || val.isEmpty()) return "";
        if (val.equals("AB")) return "ABSENT";
        try {
            int mark = Integer.parseInt(val);
            return mark >= passMark ? "PASS" : "FAIL";
        } catch (NumberFormatException e) {
            return "";
        }
    }

    private int parseMark(String val) {
        if (val == null || val.equals("-") || val.equals("AB") || val.isEmpty()) return 0;
        try {
            return Integer.parseInt(val);
        } catch (NumberFormatException e) {
            return 0;
        }
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public User getStudent() { return student; }
    public void setStudent(User student) { this.student = student; }
    public Subject getSubject() { return subject; }
    public void setSubject(Subject subject) { this.subject = subject; }
    public String getCia1() { return cia1; }
    public void setCia1(String cia1) { this.cia1 = cia1; }
    public String getCia2() { return cia2; }
    public void setCia2(String cia2) { this.cia2 = cia2; }
    public String getModelExam() { return modelExam; }
    public void setModelExam(String modelExam) { this.modelExam = modelExam; }
    public String getCia1Result() { return cia1Result; }
    public void setCia1Result(String cia1Result) { this.cia1Result = cia1Result; }
    public String getCia2Result() { return cia2Result; }
    public void setCia2Result(String cia2Result) { this.cia2Result = cia2Result; }
    public String getModelResult() { return modelResult; }
    public void setModelResult(String modelResult) { this.modelResult = modelResult; }
    public Integer getTotal() { return total; }
    public void setTotal(Integer total) { this.total = total; }
    public String getResult() { return result; }
    public void setResult(String result) { this.result = result; }
}
