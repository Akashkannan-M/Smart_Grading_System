package com.sudharsan.smartgrading.controller;

import com.sudharsan.smartgrading.dto.JwtResponse;
import com.sudharsan.smartgrading.dto.LoginRequest;
import com.sudharsan.smartgrading.model.User;
import com.sudharsan.smartgrading.repository.UserRepository;
import com.sudharsan.smartgrading.security.JwtService;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/auth")
public class AuthController {
    private static final Logger logger = LoggerFactory.getLogger(AuthController.class);

    @Autowired
    AuthenticationManager authenticationManager;

    @Autowired
    UserRepository userRepository;

    @Autowired
    JwtService jwtService;

    @PostMapping("/login")
    public ResponseEntity<?> authenticateUser(@Valid @RequestBody LoginRequest loginRequest) {
        System.out.println("[CRITICAL DEBUG] Login attempt received for username: " + loginRequest.getUsername());
        logger.info("[DEBUG] Login attempt received for username: {}", loginRequest.getUsername());
        
        try {
            // AuthenticationManager uses DaoAuthenticationProvider which uses UserDetailsService
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(loginRequest.getUsername(), loginRequest.getPassword()));

            logger.info("[DEBUG] Authentication successful for user: {}", loginRequest.getUsername());
            SecurityContextHolder.getContext().setAuthentication(authentication);
            
            String jwt = jwtService.generateToken(loginRequest.getUsername());

            // Get user info to return in response
            User user = userRepository.findByRegNo(loginRequest.getUsername())
                    .orElseGet(() -> userRepository.findByEmpId(loginRequest.getUsername())
                            .orElseThrow(() -> {
                                logger.error("[DEBUG] User not found in database after successful authentication: {}", loginRequest.getUsername());
                                return new UsernameNotFoundException("User Not Found");
                            }));

            logger.info("[DEBUG] JWT generated and returning response for user: {}", loginRequest.getUsername());

            return ResponseEntity.ok(new JwtResponse(jwt,
                    user.getId(),
                    loginRequest.getUsername(),
                    user.getName(),
                    user.getDepartment(),
                    user.getRole(),
                    user.getEmpId(),
                    user.getRegNo()));
                    
        } catch (BadCredentialsException e) {
            logger.error("[DEBUG] Authentication failed for user {}: Invalid username or password", loginRequest.getUsername());
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("message", "Error: Invalid username or password", "status", HttpStatus.UNAUTHORIZED.value()));
        } catch (AuthenticationException e) {
            logger.error("[DEBUG] Authentication error for user {}: {}", loginRequest.getUsername(), e.getMessage());
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("message", "Error: " + e.getMessage(), "status", HttpStatus.UNAUTHORIZED.value()));
        } catch (Exception e) {
            logger.error("[DEBUG] Unexpected error during authentication for user {}: {}", loginRequest.getUsername(), e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("message", "Error: An unexpected error occurred", "status", HttpStatus.INTERNAL_SERVER_ERROR.value()));
        }
    }
}
