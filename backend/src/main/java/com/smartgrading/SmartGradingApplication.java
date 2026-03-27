package com.smartgrading;

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
    public CommandLineRunner initData(com.smartgrading.service.SeedingService seedingService) {
        return args -> seedingService.seedData();
    }
}
