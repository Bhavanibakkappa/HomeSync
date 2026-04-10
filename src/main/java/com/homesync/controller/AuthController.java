package com.homesync.controller;

import com.homesync.model.User;
import com.homesync.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

/**
 * CONTROLLER layer (MVC Pattern).
 *
 * ╔═══════════════════════════════════════════════════════╗
 * ║  GRASP – Controller Pattern                          ║
 * ║  MVC   – Controller (handles HTTP, delegates to      ║
 * ║           Service, returns View name)                ║
 * ║  Member 1 – Auth + User Management                   ║
 * ╚═══════════════════════════════════════════════════════╝
 *
 * GRASP Controller:
 *   AuthController is the first point of contact for all
 *   authentication-related system events (login, register).
 *   It does NOT contain business logic — it delegates to UserService.
 *   It does NOT render HTML directly — it returns view names to Thymeleaf.
 *
 * MVC: Controller sits between View and Model/Service.
 *   View sends form data → Controller receives → Service processes
 *   → Controller adds result to Model → View renders.
 *
 * MEMBER 1 owns this class.
 */
@Controller
@RequiredArgsConstructor
public class AuthController {

    private final UserService userService;

    /** Show login page */
    @GetMapping("/login")
    public String showLoginPage() {
        return "auth/login";   // → templates/auth/login.html
    }

    /** Show registration form */
    @GetMapping("/register")
    public String showRegisterForm(Model model) {
        model.addAttribute("roles", User.Role.values());
        return "auth/register";   // → templates/auth/register.html
    }

    /** Process registration form submission */
    @PostMapping("/register")
    public String processRegister(
            @RequestParam String username,
            @RequestParam String email,
            @RequestParam String password,
            @RequestParam String fullName,
            @RequestParam User.Role role,
            RedirectAttributes redirectAttrs) {
        try {
            userService.registerUser(username, email, password, fullName, role);
            redirectAttrs.addFlashAttribute("success", "Account created! Please login.");
            return "redirect:/login";
        } catch (IllegalArgumentException e) {
            redirectAttrs.addFlashAttribute("error", e.getMessage());
            return "redirect:/register";
        }
    }

    /** Dashboard — landing page after login */
    @GetMapping("/dashboard")
    public String dashboard(@AuthenticationPrincipal UserDetails userDetails, Model model) {
        userService.findByUsername(userDetails.getUsername())
                .ifPresent(u -> model.addAttribute("currentUser", u));
        return "dashboard";   // → templates/dashboard.html
    }

    /** Admin: view all users */
    @GetMapping("/admin/users")
    public String manageUsers(Model model) {
        model.addAttribute("users", userService.getAllUsers());
        return "admin/users";
    }

    /** Minor Use Case: Edit own profile */
    @GetMapping("/profile")
    public String showProfile(@AuthenticationPrincipal UserDetails userDetails, Model model) {
        userService.findByUsername(userDetails.getUsername())
                .ifPresent(u -> model.addAttribute("user", u));
        return "auth/profile";
    }

    @PostMapping("/profile/update")
    public String updateProfile(
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestParam(required = false) String newEmail,
            @RequestParam(required = false) String newPassword,
            RedirectAttributes redirectAttrs) {
        userService.findByUsername(userDetails.getUsername()).ifPresent(u -> {
            userService.updateProfile(u.getId(), newEmail, newPassword);
        });
        redirectAttrs.addFlashAttribute("success", "Profile updated.");
        return "redirect:/profile";
    }

    /** Admin: assign role to user */
    @PostMapping("/admin/users/{id}/role")
    public String assignRole(@PathVariable Long id,
                             @RequestParam User.Role role,
                             RedirectAttributes redirectAttrs) {
        userService.assignRole(id, role);
        redirectAttrs.addFlashAttribute("success", "Role updated.");
        return "redirect:/admin/users";
    }
}
