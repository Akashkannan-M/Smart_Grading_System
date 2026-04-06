package com.sudharsan.smartgrading.model;

import jakarta.persistence.*;

@Entity
@Table(name = "subjects")
public class Subject {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String name;

    @ManyToOne
    @JoinColumn(name = "staff_id")
    private User staff;

    public Subject() {}

    public Subject(String name, User staff) {
        this.name = name;
        this.staff = staff;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public User getStaff() { return staff; }
    public void setStaff(User staff) { this.staff = staff; }
}
