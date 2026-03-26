package com.smartgrading.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "subjects")
public class Subject {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "subject_name")
    private String subjectName;

    @ManyToOne
    @JoinColumn(name = "staff_id")
    private User staff;

    public Subject() {}
    public Subject(Long id, String subjectName, User staff) {
        this.id = id;
        this.subjectName = subjectName;
        this.staff = staff;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getSubjectName() { return subjectName; }
    public void setSubjectName(String subjectName) { this.subjectName = subjectName; }
    public User getStaff() { return staff; }
    public void setStaff(User staff) { this.staff = staff; }
}
