package com.sudharsan.smartgrading.dto;

import com.sudharsan.smartgrading.model.Role;

public class JwtResponse {
    private String token;
    private Long id;
    private String username;
    private String name;
    private String department;
    private Role role;
    private String empId;
    private String regNo;

    public JwtResponse(String token, Long id, String username, String name, String department, Role role, String empId, String regNo) {
        this.token = token;
        this.id = id;
        this.username = username;
        this.name = name;
        this.department = department;
        this.role = role;
        this.empId = empId;
        this.regNo = regNo;
    }

    public String getToken() { return token; }
    public void setToken(String token) { this.token = token; }
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getDepartment() { return department; }
    public void setDepartment(String department) { this.department = department; }
    public Role getRole() { return role; }
    public void setRole(Role role) { this.role = role; }
    public String getEmpId() { return empId; }
    public void setEmpId(String empId) { this.empId = empId; }
    public String getRegNo() { return regNo; }
    public void setRegNo(String regNo) { this.regNo = regNo; }
}
