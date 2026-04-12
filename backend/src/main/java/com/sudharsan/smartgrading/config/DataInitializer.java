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
import java.util.Optional;

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

            /* Commented out as it blocks startup on large datasets
            List<Mark> allMarks = markRepository.findAll();
            for (Mark mark : allMarks) {
                boolean changed = false;

                String c1 = mark.getCia1();
                if (isNumeric(c1) && Integer.parseInt(c1) > 60) {
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
            */
        };
    }

    private void seedUsers(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        saveOrUpdateUser(userRepository, "Sujatha", Role.HOD, "CSE", null, "HOD001", "01011980@hod", passwordEncoder);
        saveOrUpdateUser(userRepository, "Ayyapan", Role.CC, "CSE", null, "CC001", "01011985@cc", passwordEncoder);

        String[] staffNames = { "Aarthi", "Siva Priyanka", "Sugashini", "Elambarathi", "Indu", "Ayyapan" };
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
        updateOrSaveSubject(subjectRepository, "Cloud Service Management", "STF001", userRepository);
        updateOrSaveSubject(subjectRepository, "Multimedia and Animation", "STF006", userRepository);
        updateOrSaveSubject(subjectRepository, "Network Security", "STF002", userRepository);
        updateOrSaveSubject(subjectRepository, "Storage Technology", "STF003", userRepository);
        updateOrSaveSubject(subjectRepository, "Object Oriented Software Engineering", "STF004", userRepository);
        updateOrSaveSubject(subjectRepository, "Embedded and IoT", "STF005", userRepository);
    }

    private void updateOrSaveSubject(SubjectRepository repo, String name, String empId, UserRepository userRepo) {
        Subject s = repo.findByName(name).orElse(new Subject(name, null));
        s.setStaff(userRepo.findByEmpId(empId).orElse(null));
        repo.save(s);
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
