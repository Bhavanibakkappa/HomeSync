package com.homesync.controller;

import com.homesync.service.SecurityService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

/**
 * CONTROLLER layer (MVC Pattern).
 *
 * GRASP – Controller Pattern:
 *   SecurityController is the FIRST point of contact for all
 *   security-related HTTP events (arm, disarm, intrusion, view logs).
 *   It does NOT contain any business logic — it ONLY delegates to SecurityService.
 *   It does NOT render HTML directly — it returns Thymeleaf view names.
 *
 * MVC:
 *   View (security/dashboard.html) sends form POST →
 *   Controller receives → delegates to Service →
 *   Controller adds result to Model → Thymeleaf renders View.
 *
 * MEMBER 3 owns this class.
 */
@Controller
@RequestMapping("/security")
public class SecurityController {

    private final SecurityService securityService;

    // Constructor injection — no @Autowired needed (Spring best practice)
    public SecurityController(SecurityService securityService) {
        this.securityService = securityService;
    }

    // ── GET endpoints — render views ─────────────────────────────────────────

    /**
     * Major Use Case: Monitor Security — shows dashboard with logs + controls.
     * Passes all logs AND current system state to the view.
     */
    @GetMapping
    public String securityDashboard(Model model) {
        model.addAttribute("logs", securityService.getAllLogs());
        model.addAttribute("currentState", securityService.getCurrentState());
        return "security/dashboard";    // → templates/security/dashboard.html
    }

    /**
     * Minor Use Case: View full security log history (separate page).
     */
    @GetMapping("/logs")
    public String viewLogs(Model model) {
        model.addAttribute("logs", securityService.getAllLogs());
        return "security/logs";         // → templates/security/logs.html
    }

    // ── POST endpoints — handle form submissions, delegate to service ─────────

    /**
     * Major Use Case: Arm the security system.
     * GRASP Controller: receives the event, delegates to SecurityService, redirects.
     */
    @PostMapping("/arm")
    public String arm(@RequestParam String location,
                      RedirectAttributes redirectAttributes) {
        securityService.armSystem(location);
        redirectAttributes.addFlashAttribute("success",
                "Security system ARMED at: " + location);
        return "redirect:/security";
    }

    /**
     * Major Use Case: Disarm the security system.
     */
    @PostMapping("/disarm")
    public String disarm(@RequestParam String location,
                         RedirectAttributes redirectAttributes) {
        securityService.disarmSystem(location);
        redirectAttributes.addFlashAttribute("success",
                "Security system DISARMED at: " + location);
        return "redirect:/security";
    }

    /**
     * Major Use Case: Trigger Intrusion Alert (simulates sensor/motion trigger).
     * Only ADMIN should see this button in the view (controlled via sec:authorize).
     */
    @PostMapping("/trigger")
    public String triggerIntrusion(@RequestParam String location,
                                   RedirectAttributes redirectAttributes) {
        securityService.triggerIntrusion(location);
        redirectAttributes.addFlashAttribute("warning",
                "INTRUSION DETECTED at: " + location + " — External service notified!");
        return "redirect:/security";
    }

    /**
     * Minor Use Case: Reset security system back to DISARMED state.
     */
    @PostMapping("/reset")
    public String resetSystem(@RequestParam String location,
                              RedirectAttributes redirectAttributes) {
        securityService.resetSystem(location);
        redirectAttributes.addFlashAttribute("success",
                "Security system RESET at: " + location);
        return "redirect:/security";
    }

    /**
     * Admin only: Delete a specific security log entry.
     * Admin use case — protected by Spring Security at URL level.
     */
    @PostMapping("/delete/{id}")
    public String deleteLog(@PathVariable Long id,
                            RedirectAttributes redirectAttributes) {
        securityService.deleteLog(id);
        redirectAttributes.addFlashAttribute("success", "Log entry deleted.");
        return "redirect:/security/logs";
    }
}
