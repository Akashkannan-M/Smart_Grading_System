package com.smartgrading.controller;

import com.smartgrading.dto.AuthRequest;
import com.smartgrading.dto.AuthResponse;
import com.smartgrading.entity.User;
import com.smartgrading.repository.UserRepository;
import com.smartgrading.security.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = "*")
public class AuthController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private JwtUtil jwtUtil;

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody AuthRequest request) {
        User user = userRepository.findByUsername(request.getUsername());
        if (user != null && user.getPassword().equals(request.getPassword())) {
            String token = jwtUtil.generateToken(user.getUsername());
            return ResponseEntity.ok(new AuthResponse(
                token, 
                user.getId(), 
                user.getName(), 
                user.getUsername(), 
                user.getRole().toString(), 
                user.getDepartment(),
                user.getCurrentYear() != null ? user.getCurrentYear() : "1st Year"
            ));
        }
        return ResponseEntity.status(401).body("Invalid credentials.");
    }
}
