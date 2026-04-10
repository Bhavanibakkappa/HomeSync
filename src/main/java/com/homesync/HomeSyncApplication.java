package com.homesync;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * HomeSync – Smart Home Controller
 * Entry point for the Spring Boot application.
 *
 * MVC Pattern: Spring Boot auto-configures the MVC framework.
 * All Controllers, Services, and Models follow the MVC separation.
 */
@SpringBootApplication
public class HomeSyncApplication {
    public static void main(String[] args) {
        SpringApplication.run(HomeSyncApplication.class, args);
    }
}
