package com.smartgrading;

import com.smartgrading.entity.User;
import com.smartgrading.entity.Subject;
import com.smartgrading.repository.UserRepository;
import com.smartgrading.repository.SubjectRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class SmartGradingApplication {

    public static void main(String[] args) {
        SpringApplication.run(SmartGradingApplication.class, args);
    }

    @Bean
    public CommandLineRunner initData(UserRepository userRepository, SubjectRepository subjectRepository) {
        return args -> {
            try {
                // Ensure HOD (Sujatha) exists
                if (userRepository.findByUsername("sujatha") == null) {
                    userRepository.save(new User(null, "Sujatha", "sujatha", "01011980@hod", User.Role.HOD, "CSE", "4th Year"));
                }

                // Ensure CC (Ayyapan) exists with specific password
                if (userRepository.findByUsername("ayyapan_cc") == null) {
                    userRepository.save(new User(null, "Ayyapan", "ayyapan_cc", "01011980@cc", User.Role.CC, "CSE", "2nd Year"));
                }

                // Ensure all 6 Staff and their subjects exist precisely as requested
                createOrUpdateStaff("Aarthi", "aarthi", "01011980@staff", "Cloud Service Management", userRepository, subjectRepository);
                createOrUpdateStaff("Ayyapan", "ayyapan", "01011980@staff", "Multimedia and Animation", userRepository, subjectRepository);
                createOrUpdateStaff("Siva Priyanka", "siva", "01011980@staff", "Network Security", userRepository, subjectRepository);
                createOrUpdateStaff("Sugashini", "suga", "01011980@staff", "Storage Technology", userRepository, subjectRepository);
                createOrUpdateStaff("Elambarathi", "elambarathi", "01011980@staff", "Object Oriented Software Engineering", userRepository, subjectRepository);
                createOrUpdateStaff("Indu", "indu", "01011980@staff", "Embedded and IoT", userRepository, subjectRepository);

                // Ensure Students exist
                if (userRepository.findByUsername("2024001") == null) {
                    for (int i = 1; i <= 25; i++) {
                        String id = String.format("%03d", i);
                        userRepository.save(new User(null, "Student " + id, "2024" + id, "01012000@student", User.Role.STUDENT, "CSE", "1st Year"));
                    }
                }
                System.out.println(">>> SEEDING COMPLETE: ALL STAFF AND CC INITIALIZED <<<");
            } catch (Exception e) {
                System.out.println(">>> SEEDING ERROR: " + e.getMessage());
            }
        };
    }

    private void createOrUpdateStaff(String name, String username, String password, String subName, UserRepository ur, SubjectRepository sr) {
        User staff = ur.findByUsername(username);
        if (staff == null) {
            staff = new User(null, name, username, password, User.Role.STAFF, "CSE", "3rd Year");
            staff = ur.save(staff);
        }
        
        // Ensure subject is assigned to this exact staff
        Subject sub = sr.findAll().stream()
                        .filter(s -> s.getSubjectName().equalsIgnoreCase(subName))
                        .findFirst().orElse(null);
        if (sub == null) {
            sub = new Subject(null, subName, staff);
            sr.save(sub);
        } else {
            sub.setStaff(staff);
            sr.save(sub);
        }
    }
}
