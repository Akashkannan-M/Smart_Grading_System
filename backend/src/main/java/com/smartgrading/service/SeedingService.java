package com.smartgrading.service;

import com.smartgrading.entity.User;
import com.smartgrading.entity.Subject;
import com.smartgrading.repository.UserRepository;
import com.smartgrading.repository.SubjectRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class SeedingService {

    @Autowired private UserRepository userRepository;
    @Autowired private SubjectRepository subjectRepository;

    @Transactional
    public void seedData() {
        try {
            // HOD
            if (userRepository.findByUsername("sujatha") == null) {
                userRepository.save(new User(null, "Sujatha", "sujatha", "01011980@hod", User.Role.HOD, "CSE", "4th Year"));
            }

            // CC
            if (userRepository.findByUsername("ayyapan_cc") == null) {
                userRepository.save(new User(null, "Ayyapan", "ayyapan_cc", "01011980@cc", User.Role.CC, "CSE", "2nd Year"));
            }

            // Staff & Subjects
            createOrUpdateStaff("Aarthi", "aarthi", "01011980@staff", "Cloud Service Management");
            createOrUpdateStaff("Ayyapan", "ayyapan", "01011980@staff", "Multimedia and Animation");
            createOrUpdateStaff("Siva Priyanka", "siva", "01011980@staff", "Network Security");
            createOrUpdateStaff("Sugashini", "suga", "01011980@staff", "Storage Technology");
            createOrUpdateStaff("Elambarathi", "elambarathi", "01011980@staff", "Object Oriented Software Engineering");
            createOrUpdateStaff("Indu", "indu", "01011980@staff", "Embedded and IoT");

            // Students
            if (userRepository.findByUsername("2024001") == null) {
                for (int i = 1; i <= 25; i++) {
                    String id = String.format("%03d", i);
                    userRepository.save(new User(null, "Student " + id, "2024" + id, "01012000@student", User.Role.STUDENT, "CSE", "1st Year"));
                }
            }
            System.out.println(">>> SEEDING COMPLETE: DATABASE SYNCHRONIZED <<<");
        } catch (Exception e) {
            System.out.println(">>> SEEDING FAILED IN TRANSACTION: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void createOrUpdateStaff(String name, String username, String password, String subName) {
        User staff = userRepository.findByUsername(username);
        if (staff == null) {
            staff = new User(null, name, username, password, User.Role.STAFF, "CSE", "3rd Year");
            staff = userRepository.save(staff);
        }
        
        Subject sub = subjectRepository.findAll().stream()
                        .filter(s -> s.getSubjectName().equalsIgnoreCase(subName))
                        .findFirst().orElse(null);
        if (sub == null) {
            sub = new Subject(null, subName, staff);
            subjectRepository.save(sub);
        } else {
            sub.setStaff(staff);
            subjectRepository.save(sub);
        }
    }
}
