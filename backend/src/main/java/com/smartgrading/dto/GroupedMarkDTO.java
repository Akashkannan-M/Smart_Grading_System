package com.smartgrading.dto;

public class GroupedMarkDTO {
    private String subjectName;
    private Integer cia1;
    private Integer cia2;
    private Integer model;
    private Integer total;
    private boolean pass;

    public GroupedMarkDTO() {
        this.cia1 = 0;
        this.cia2 = 0;
        this.model = 0;
        this.total = 0;
    }

    public GroupedMarkDTO(String subjectName, Integer cia1, Integer cia2, Integer model) {
        this.subjectName = subjectName;
        this.cia1 = cia1 != null ? cia1 : 0;
        this.cia2 = cia2 != null ? cia2 : 0;
        this.model = model != null ? model : 0;
        this.total = this.cia1 + this.cia2 + this.model;
        // Pass Rule: CIA1 >= 30, CIA2 >= 30, MODEL >= 45
        this.pass = this.cia1 >= 30 && this.cia2 >= 30 && this.model >= 45;
    }

    // Boilerplate for Java 26
    public String getSubjectName() { return subjectName; }
    public void setSubjectName(String subjectName) { this.subjectName = subjectName; }
    public Integer getCia1() { return cia1; }
    public void setCia1(Integer cia1) { this.cia1 = cia1; }
    public Integer getCia2() { return cia2; }
    public void setCia2(Integer cia2) { this.cia2 = cia2; }
    public Integer getModel() { return model; }
    public void setModel(Integer model) { this.model = model; }
    public Integer getTotal() { return total; }
    public void setTotal(Integer total) { this.total = total; }
    public boolean isPass() { return pass; }
    public void setPass(boolean pass) { this.pass = pass; }
}
