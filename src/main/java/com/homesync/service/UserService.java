package com.homesync.service;

import com.homesync.model.User;
import com.homesync.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

/**
 * SERVICE layer (MVC Pattern — business logic lives here, NOT in controller).
 *
 * ╔══════════════════════════════════════════════════╗
 * ║  SOLID – SRP (Single Responsibility Principle)  ║
 * ║  Member 1 – Manage User Accounts                ║
 * ╚══════════════════════════════════════════════════╝
 *
 * UserService has ONE responsibility: User account management.
 * - It does NOT handle HTTP requests (that's AuthController)
 * - It does NOT handle DB queries directly (that's UserRepository)
 * - It does NOT handle security config (that's SecurityConfig)
 *
 * SOLID – DIP: This class depends on UserRepository INTERFACE,
 * not a concrete implementation. Spring injects the right impl.
 *
 * MEMBER 1 owns this class.
 */
@Service
@RequiredArgsConstructor
@Transactional
public class UserService {

    // DIP: depends on interface, not concrete class
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    /**
     * Major Use Case: Register a new user.
     * Validates uniqueness, hashes password, persists.
     */
    public User registerUser(String username, String email,
                             String rawPassword, String fullName,
                             User.Role role) {
        if (userRepository.existsByUsername(username)) {
            throw new IllegalArgumentException("Username already taken: " + username);
        }
        if (userRepository.existsByEmail(email)) {
            throw new IllegalArgumentException("Email already registered: " + email);
        }

        User user = new User();
        user.setUsername(username);
        user.setEmail(email);
        user.setPassword(passwordEncoder.encode(rawPassword));
        user.setFullName(fullName);
        user.setRole(role);
        user.setActive(true);

        return userRepository.save(user);
    }

    /**
     * Minor Use Case: Edit user profile (password / email).
     */
    public User updateProfile(Long userId, String newEmail, String newPassword) {
        User user = getUserById(userId);

        if (newEmail != null && !newEmail.isBlank()) {
            user.setEmail(newEmail);
        }
        if (newPassword != null && !newPassword.isBlank()) {
            user.setPassword(passwordEncoder.encode(newPassword));
        }

        return userRepository.save(user);
    }

    /** Admin only: assign a role to a user. */
    public User assignRole(Long userId, User.Role role) {
        User user = getUserById(userId);
        user.setRole(role);
        return userRepository.save(user);
    }

    /** Admin only: deactivate a user. */
    public void deactivateUser(Long userId) {
        User user = getUserById(userId);
        user.setActive(false);
        userRepository.save(user);
    }

    public User getUserById(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found: " + id));
    }

    public Optional<User> findByUsername(String username) {
        return userRepository.findByUsername(username);
    }

    public List<User> getAllUsers() {
        return userRepository.findAll();
    }
}
