package com.smartgrading.controller;

import com.smartgrading.dto.AuthRequest;
import com.smartgrading.dto.AuthResponse;
import com.smartgrading.entity.User;
import com.smartgrading.repository.UserRepository;
import com.smartgrading.security.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import java.util.Optional;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private UserRepository userRepository;

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody AuthRequest authRequest) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(authRequest.getUsername(), authRequest.getPassword())
        );

        Optional<User> userOpt = userRepository.findByUsername(authRequest.getUsername());
        if (userOpt.isPresent()) {
            User user = userOpt.get();
            final String jwt = jwtUtil.generateToken(
                    org.springframework.security.core.userdetails.User.withUsername(user.getUsername()).password(user.getPassword()).authorities("ROLE_" + user.getRole().name()).build(),
                    user.getRole().name(),
                    user.getName()
            );

            return ResponseEntity.ok(new AuthResponse(
                    jwt,
                    user.getId(),
                    user.getName(),
                    user.getUsername(),
                    user.getRole().name(),
                    user.getDepartment()
            ));
        }

        return ResponseEntity.badRequest().body("User not found");
    }
}
