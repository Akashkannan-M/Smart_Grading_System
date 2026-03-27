package com.smartgrading.dto;

import java.util.List;

public class DashboardSummaryDTO {
    private List<SubjectRankDTO> subjectRankings;
    private OverallStatsDTO statistics;
    private StudentProfileDTO personalProfile;

    public DashboardSummaryDTO() {}

    public List<SubjectRankDTO> getSubjectRankings() { return subjectRankings; }
    public void setSubjectRankings(List<SubjectRankDTO> subjectRankings) { this.subjectRankings = subjectRankings; }
    public OverallStatsDTO getStatistics() { return statistics; }
    public void setStatistics(OverallStatsDTO statistics) { this.statistics = statistics; }
    public StudentProfileDTO getPersonalProfile() { return personalProfile; }
    public void setPersonalProfile(StudentProfileDTO personalProfile) { this.personalProfile = personalProfile; }

    public static class SubjectRankDTO {
        private String subjectName;
        private Long subjectId; // Added for Staff Save
        private List<StudentMarkDTO> ranking;
        private double average;
        private double passPercentage;

        public String getSubjectName() { return subjectName; }
        public void setSubjectName(String subjectName) { this.subjectName = subjectName; }
        public Long getSubjectId() { return subjectId; }
        public void setSubjectId(Long subjectId) { this.subjectId = subjectId; }
        public List<StudentMarkDTO> getRanking() { return ranking; }
        public void setRanking(List<StudentMarkDTO> ranking) { this.ranking = ranking; }
        public double getAverage() { return average; }
        public void setAverage(double average) { this.average = average; }
        public double getPassPercentage() { return passPercentage; }
        public void setPassPercentage(double passPercentage) { this.passPercentage = passPercentage; }
    }

    public static class StudentMarkDTO {
        private String name;
        private String username;
        private Long studentId; // Added for Staff Save
        private Integer marks;
        private boolean pass;
        private int rank;

        public String getName() { return name; }
        public void setName(String name) { this.name = name; }
        public String getUsername() { return username; }
        public void setUsername(String username) { this.username = username; }
        public Long getStudentId() { return studentId; }
        public void setStudentId(Long studentId) { this.studentId = studentId; }
        public Integer getMarks() { return marks; }
        public void setMarks(Integer marks) { this.marks = marks; }
        public boolean isPass() { return pass; }
        public void setPass(boolean pass) { this.pass = pass; }
        public int getRank() { return rank; }
        public void setRank(int rank) { this.rank = rank; }
    }

    public static class OverallStatsDTO {
        private double globalAverage;
        private double globalPassRate;

        public double getGlobalAverage() { return globalAverage; }
        public void setGlobalAverage(double globalAverage) { this.globalAverage = globalAverage; }
        public double getGlobalPassRate() { return globalPassRate; }
        public void setGlobalPassRate(double globalPassRate) { this.globalPassRate = globalPassRate; }
    }

    public static class StudentProfileDTO {
        private String name;
        private String role;
        private String department;
        private Integer myGlobalRank;
        private Integer myTotalMarks;

        public String getName() { return name; }
        public void setName(String name) { this.name = name; }
        public String getRole() { return role; }
        public void setRole(String role) { this.role = role; }
        public String getDepartment() { return department; }
        public void setDepartment(String department) { this.department = department; }
        public Integer getMyGlobalRank() { return myGlobalRank; }
        public void setMyGlobalRank(Integer myGlobalRank) { this.myGlobalRank = myGlobalRank; }
        public Integer getMyTotalMarks() { return myTotalMarks; }
        public void setMyTotalMarks(Integer myTotalMarks) { this.myTotalMarks = myTotalMarks; }
    }
}
