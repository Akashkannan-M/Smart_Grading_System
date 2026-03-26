package com.smartgrading.controller;

import com.smartgrading.entity.User;
import com.smartgrading.entity.Subject;
import com.smartgrading.repository.UserRepository;
import com.smartgrading.repository.SubjectRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import java.util.Arrays;
import java.util.List;

@RestController
@RequestMapping("/api/setup")
public class SetupController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private SubjectRepository subjectRepository;

    @GetMapping
    public ResponseEntity<?> setupInitialData() {
        if (userRepository.count() > 0) {
            return ResponseEntity.ok("Database already initialized.");
        }

        // Create HOD
        User hod = new User();
        hod.setName("Sujatha");
        hod.setUsername("sujatha");
        // For simplicity assuming DOB is 01011980
        hod.setPassword("01011980@hod");
        hod.setRole(User.Role.HOD);
        hod.setDepartment("CSE");
        userRepository.save(hod);

        // Create CC
        User cc = new User();
        cc.setName("Ayyapan");
        cc.setUsername("ayyapan_cc");
        cc.setPassword("01011980@cc");
        cc.setRole(User.Role.CC);
        cc.setDepartment("CSE");
        userRepository.save(cc);

        // Create Staff
        createStaff("Aarthi", "aarthi", "01011980@staff", "Cloud Service Management");
        createStaff("Ayyapan", "ayyapan", "01011980@staff", "Multimedia and Animation");
        createStaff("Siva Priyanka", "siva", "01011980@staff", "Network Security");
        createStaff("Sugashini", "suga", "01011980@staff", "Storage Technology");
        createStaff("Elambarathi", "elambarathi", "01011980@staff", "Object Oriented Software Engineering");
        createStaff("Indu", "indu", "01011980@staff", "Embedded and IoT");

        // Create 25 Students
        for(int i = 1; i <= 25; i++) {
            User student = new User();
            String idStr = String.format("%03d", i);
            student.setName("Student " + idStr);
            student.setUsername("2024" + idStr);
            student.setPassword("01012000@student");
            student.setRole(User.Role.STUDENT);
            student.setDepartment("CSE");
            userRepository.save(student);
        }

        return ResponseEntity.ok("Database Initialized Successfully with Dummy Data.");
    }

    private void createStaff(String name, String username, String password, String subjectName) {
        User staff = new User();
        staff.setName(name);
        staff.setUsername(username);
        staff.setPassword(password);
        staff.setRole(User.Role.STAFF);
        staff.setDepartment("CSE");
        staff = userRepository.save(staff);

        Subject subject = new Subject();
        subject.setSubjectName(subjectName);
        subject.setStaff(staff);
        subjectRepository.save(subject);
    }
}
