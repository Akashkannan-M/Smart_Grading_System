package com.sudharsan.smartgrading.model;

import jakarta.persistence.*;

@Entity
@Table(name = "users")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Role role;

    @Column(nullable = false)
    private String department;
    
    @Column(name = "reg_no", unique = true)
    private String regNo;

    @Column(name = "emp_id", unique = true)
    private String empId;

    @Column(nullable = false)
    private String password;

    public User() {}

    public User(String name, Role role, String department, String regNo, String empId, String password) {
        this.name = name;
        this.role = role;
        this.department = department;
        this.regNo = regNo;
        this.empId = empId;
        this.password = password;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public Role getRole() { return role; }
    public void setRole(Role role) { this.role = role; }
    public String getDepartment() { return department; }
    public void setDepartment(String department) { this.department = department; }
    public String getRegNo() { return regNo; }
    public void setRegNo(String regNo) { this.regNo = regNo; }
    public String getEmpId() { return empId; }
    public void setEmpId(String empId) { this.empId = empId; }
    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }
}
