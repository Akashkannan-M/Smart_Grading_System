package com.smartgrading.dto;

import java.util.List;

public class StudentPerformanceDTO {
    private Long studentId;
    private String name;
    private String username;
    private String currentYear;
    private List<GroupedMarkDTO> subjectMarks;
    private Integer overallTotal;
    private double overallAverage;
    private boolean overallPass;
    private int rank;

    public StudentPerformanceDTO() {}

    public Long getStudentId() { return studentId; }
    public void setStudentId(Long studentId) { this.studentId = studentId; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }
    public String getCurrentYear() { return currentYear; }
    public void setCurrentYear(String currentYear) { this.currentYear = currentYear; }
    public List<GroupedMarkDTO> getSubjectMarks() { return subjectMarks; }
    public void setSubjectMarks(List<GroupedMarkDTO> subjectMarks) { this.subjectMarks = subjectMarks; }
    public Integer getOverallTotal() { return overallTotal; }
    public void setOverallTotal(Integer overallTotal) { this.overallTotal = overallTotal; }
    public double getOverallAverage() { return overallAverage; }
    public void setOverallAverage(double overallAverage) { this.overallAverage = overallAverage; }
    public boolean isOverallPass() { return overallPass; }
    public void setOverallPass(boolean overallPass) { this.overallPass = overallPass; }
    public int getRank() { return rank; }
    public void setRank(int rank) { this.rank = rank; }
}
