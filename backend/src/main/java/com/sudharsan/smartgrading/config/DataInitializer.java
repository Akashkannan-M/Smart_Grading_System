package com.sudharsan.smartgrading.config;

import com.sudharsan.smartgrading.model.Role;
import com.sudharsan.smartgrading.model.Subject;
import com.sudharsan.smartgrading.model.User;
import com.sudharsan.smartgrading.repository.SubjectRepository;
import com.sudharsan.smartgrading.repository.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import com.sudharsan.smartgrading.model.Mark;
import com.sudharsan.smartgrading.repository.MarkRepository;

@Configuration
public class DataInitializer {

    @Bean
    @Transactional
    public CommandLineRunner loadData(UserRepository userRepository,
            SubjectRepository subjectRepository,
            MarkRepository markRepository,
            PasswordEncoder passwordEncoder) {
        return args -> {
            // Seeding users and subjects (Keep existing logic)
            seedUsers(userRepository, passwordEncoder);
            seedSubjects(subjectRepository, userRepository);

            // Existing mark conversion (Scale from 100 to 60)
            // Note: This logic assumes marks > 60 are definitely on a 100-point scale.
            // For marks <= 60, it's ambiguous, but we will scale those too if needed.
            // To prevent re-running this every time, you might want to remove it after one
            // successful run.
            List<Mark> allMarks = markRepository.findAll();
            for (Mark mark : allMarks) {
                boolean changed = false;

                String c1 = mark.getCia1();
                if (isNumeric(c1) && Integer.parseInt(c1) > 60) { // Heuristic: definitely needs scaling
                    mark.setCia1(scaleMark(c1));
                    changed = true;
                }

                String c2 = mark.getCia2();
                if (isNumeric(c2) && Integer.parseInt(c2) > 60) {
                    mark.setCia2(scaleMark(c2));
                    changed = true;
                }

                if (changed) {
                    markRepository.save(mark);
                }
            }
        };
    }

    private void seedUsers(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        saveOrUpdateUser(userRepository, "Sujatha", Role.HOD, "CSE", null, "HOD001", "01011980@hod", passwordEncoder);
        saveOrUpdateUser(userRepository, "Ayyapan", Role.CC, "CSE", null, "CC001", "01011985@cc", passwordEncoder);

        String[] staffNames = { "Aarthi", "Siva Priyanka", "Sugashini", "Elambarathi", "Indu" };
        for (int i = 0; i < staffNames.length; i++) {
            saveOrUpdateUser(userRepository, staffNames[i], Role.STAFF, "CSE", null, "STF00" + (i + 1),
                    "01011990@staff", passwordEncoder);
        }

        for (int i = 1; i <= 25; i++) {
            String regNo = String.format("814423104%03d", i);
            saveOrUpdateUser(userRepository, "Student" + i, Role.STUDENT, "CSE", regNo, null, "01012003@student",
                    passwordEncoder);
        }
    }

    private void seedSubjects(SubjectRepository subjectRepository, UserRepository userRepository) {
        if (subjectRepository.count() == 0) {
            subjectRepository
                    .save(new Subject("Cloud Service Management", userRepository.findByEmpId("STF001").orElse(null)));
            subjectRepository
                    .save(new Subject("Multimedia and Animation", userRepository.findByEmpId("CC001").orElse(null)));
            subjectRepository.save(new Subject("Network Security", userRepository.findByEmpId("STF002").orElse(null)));
            subjectRepository
                    .save(new Subject("Storage Technology", userRepository.findByEmpId("STF003").orElse(null)));
            subjectRepository.save(new Subject("Object Oriented Software Engineering",
                    userRepository.findByEmpId("STF004").orElse(null)));
            subjectRepository.save(new Subject("Embedded and IoT", userRepository.findByEmpId("STF005").orElse(null)));
        }
    }

    private boolean isNumeric(String str) {
        if (str == null || str.isEmpty() || str.equals("-") || str.equals("AB"))
            return false;
        return str.chars().allMatch(Character::isDigit);
    }

    private String scaleMark(String val) {
        try {
            int old = Integer.parseInt(val);
            int scaled = (int) Math.round((old / 100.0) * 60.0);
            return String.valueOf(scaled);
        } catch (Exception e) {
            return val;
        }
    }

    private void saveOrUpdateUser(UserRepository repo, String name, Role role, String dept, String regNo, String empId,
            String rawPassword, PasswordEncoder encoder) {
        User user = null;
        if (empId != null) {
            user = repo.findByEmpId(empId).orElse(null);
        } else if (regNo != null) {
            user = repo.findByRegNo(regNo).orElse(null);
        }

        if (user == null) {
            user = new User(name, role, dept, regNo, empId, encoder.encode(rawPassword));
        } else {
            user.setName(name);
            user.setRole(role);
            user.setDepartment(dept);
            user.setPassword(encoder.encode(rawPassword));
        }
        repo.save(user);
    }
}
