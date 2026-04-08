package com.sudharsan.smartgrading.controller;

import com.sudharsan.smartgrading.model.Role;
import com.sudharsan.smartgrading.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/dashboard")
public class DashboardController {

    @Autowired
    private UserRepository userRepository;


    @GetMapping("/hod")
    public Map<String, Object> getHodDashboard() {
        Map<String, Object> stats = new HashMap<>();
        long totalStudents = userRepository.findByRole(Role.STUDENT).size();
        stats.put("totalStudents", totalStudents);
        stats.put("message", "HOD Dashboard Data Loaded Successfully");
        stats.put("status", "success");
        return stats;
    }

    @GetMapping("/cc")
    public Map<String, Object> getCcDashboard() {
        Map<String, Object> stats = new HashMap<>();
        long totalStudents = userRepository.findByRole(Role.STUDENT).size();
        stats.put("totalStudents", totalStudents);
        stats.put("message", "CC Dashboard Data Loaded Successfully");
        stats.put("status", "success");
        return stats;
    }
}
