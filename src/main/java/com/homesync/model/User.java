package com.homesync.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

/**
 * MODEL layer (MVC Pattern).
 *
 * SOLID – SRP (Single Responsibility Principle):
 *   This class has ONE job: represent a User in the system.
 *   It does NOT handle authentication logic, password hashing,
 *   or business rules — those live in UserService.
 *
 * MEMBER 1 owns this class.
 */
@Entity
@Table(name = "users")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String username;

    @Column(nullable = false)
    private String password;   // stored as BCrypt hash

    @Column(nullable = false, unique = true)
    private String email;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Role role;

    private String fullName;

    private boolean active = true;

    public enum Role {
        ADMIN, RESIDENT
    }
}
