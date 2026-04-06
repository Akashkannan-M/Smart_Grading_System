package com.sudharsan.smartgrading.security;

import com.sudharsan.smartgrading.model.User;
import com.sudharsan.smartgrading.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Collections;

@Service
public class CustomUserDetailsService implements UserDetailsService {
    private static final Logger logger = LoggerFactory.getLogger(CustomUserDetailsService.class);

    @Autowired
    private UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        logger.debug("Attempting to load user by username: {}", username);
        
        // Try Registration number
        User user = userRepository.findByRegNo(username).orElse(null);
        if (user != null) {
            logger.info("User found by registration number: {}", username);
        } else {
            // Try Employee ID
            user = userRepository.findByEmpId(username).orElse(null);
            if (user != null) {
                logger.info("User found by employee ID: {}", username);
            }
        }
        
        if (user == null) {
            logger.warn("User not found in database with username: {}", username);
            throw new UsernameNotFoundException("User not found with username: " + username);
        }

        return new org.springframework.security.core.userdetails.User(
                username,
                user.getPassword(),
                Collections.singleton(new SimpleGrantedAuthority("ROLE_" + user.getRole().name()))
        );
    }
}
