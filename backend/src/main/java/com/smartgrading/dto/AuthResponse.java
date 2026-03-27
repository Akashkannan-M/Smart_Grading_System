package com.smartgrading.dto;

public class AuthResponse {
    private String token;
    private Long id;
    private String name;
    private String username;
    private String role;
    private String department;
    private String currentYear;

    public AuthResponse() {}
    public AuthResponse(String token, Long id, String name, String username, String role, String department, String currentYear) {
        this.token = token;
        this.id = id;
        this.name = name;
        this.username = username;
        this.role = role;
        this.department = department;
        this.currentYear = currentYear;
    }

    public String getToken() { return token; }
    public void setToken(String token) { this.token = token; }
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }
    public String getRole() { return role; }
    public void setRole(String role) { this.role = role; }
    public String getDepartment() { return department; }
    public void setDepartment(String department) { this.department = department; }
    public String getCurrentYear() { return currentYear; }
    public void setCurrentYear(String currentYear) { this.currentYear = currentYear; }
}
