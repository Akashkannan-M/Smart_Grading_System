package com.sudharsan.smartgrading.controller;

import com.sudharsan.smartgrading.model.Role;
import com.sudharsan.smartgrading.model.User;
import com.sudharsan.smartgrading.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.HashMap;

@RestController
@RequestMapping("/api/users")
public class UserController {

    @Autowired
    private UserRepository userRepository;

    @GetMapping("/students")
    public List<User> getAllStudents() {
        return userRepository.findByRole(Role.STUDENT);
    }

    @GetMapping("/stats")
    public Map<String, Object> getStats() {
        Map<String, Object> stats = new HashMap<>();
        long totalStudents = userRepository.findByRole(Role.STUDENT).size();
        stats.put("totalStudents", totalStudents);
        // Note: For real stats we should calculate pass percentage
        return stats;
    }
}
