package com.homesync.controller;

import com.homesync.service.DeviceService;
import com.homesync.service.ScheduleService;
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
 * GRASP – Controller Pattern:
 *   ScheduleController is the FIRST point of contact for all
 *   schedule-related HTTP events (create, delete, toggle, undo).
 *   It does NOT contain any business logic — it ONLY delegates to ScheduleService.
 *   It does NOT render HTML directly — it returns Thymeleaf view names.
 *
 * MVC:
 *   View (schedules/list.html) sends form POST →
 *   Controller receives → delegates to Service →
 *   Controller adds result to Model → Thymeleaf renders View.
 *
 * MEMBER 4 owns this class.
 */
@Controller
@RequestMapping("/schedules")
@RequiredArgsConstructor
public class ScheduleController {

    private final ScheduleService scheduleService;
    private final UserService userService;
    private final DeviceService deviceService;

    // ── GET endpoints — render views ─────────────────────────────────────────

    /**
     * Major Use Case: Schedule Automation — view all schedules.
     * Loads current user's schedules and available devices for the form.
     */
    @GetMapping
    public String listSchedules(@AuthenticationPrincipal UserDetails userDetails,
                                Model model) {
        userService.findByUsername(userDetails.getUsername()).ifPresent(user -> {
            model.addAttribute("schedules", scheduleService.getSchedulesByUser(user));
            model.addAttribute("currentUser", user);
        });
        model.addAttribute("devices", deviceService.getAllDevices());
        model.addAttribute("actions", new String[]{"ON", "OFF", "DIM", "BRIGHTEN"});
        model.addAttribute("dayOptions",
                new String[]{"MON", "TUE", "WED", "THU", "FRI", "SAT", "SUN"});
        return "schedules/list";   // → templates/schedules/list.html
    }

    /**
     * Show add schedule form.
     */
    @GetMapping("/add")
    public String showAddForm(@AuthenticationPrincipal UserDetails userDetails,
                              Model model) {
        model.addAttribute("devices", deviceService.getAllDevices());
        model.addAttribute("actions", new String[]{"ON", "OFF", "DIM", "BRIGHTEN"});
        model.addAttribute("dayOptions",
                new String[]{"MON", "TUE", "WED", "THU", "FRI", "SAT", "SUN"});
        return "schedules/add";    // → templates/schedules/add.html
    }

    // ── POST endpoints — handle form submissions, delegate to service ─────────

    /**
     * Major Use Case: Create a new schedule.
     * GRASP Controller: receives the HTTP event, delegates to ScheduleService.
     */
    @PostMapping("/add")
    public String createSchedule(
            @RequestParam String taskName,
            @RequestParam String action,
            @RequestParam(required = false) String scheduledTime,
            @RequestParam(required = false) String daysOfWeek,
            @RequestParam(required = false) Long deviceId,
            @AuthenticationPrincipal UserDetails userDetails,
            RedirectAttributes redirectAttributes) {

        try {
            userService.findByUsername(userDetails.getUsername()).ifPresent(user ->
                    scheduleService.createSchedule(
                            taskName, action, scheduledTime, daysOfWeek, deviceId, user)
            );
            redirectAttributes.addFlashAttribute("success",
                    "Schedule '" + taskName + "' created successfully!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error",
                    "Failed to create schedule: " + e.getMessage());
        }
        return "redirect:/schedules";
    }

    /**
     * Minor Use Case: Undo Last Command — deactivates the most recent schedule.
     */
    @PostMapping("/undo")
    public String undoLastSchedule(
            @AuthenticationPrincipal UserDetails userDetails,
            RedirectAttributes redirectAttributes) {

        userService.findByUsername(userDetails.getUsername()).ifPresent(user -> {
            String result = scheduleService.undoLastSchedule(user);
            redirectAttributes.addFlashAttribute("success", result);
        });
        return "redirect:/schedules";
    }

    /**
     * Toggle a schedule active or inactive.
     */
    @PostMapping("/{id}/toggle")
    public String toggleSchedule(@PathVariable Long id,
                                 RedirectAttributes redirectAttributes) {
        scheduleService.toggleSchedule(id);
        redirectAttributes.addFlashAttribute("success", "Schedule status updated.");
        return "redirect:/schedules";
    }

    /**
     * Admin only: Delete a schedule.
     */
    @PostMapping("/{id}/delete")
    public String deleteSchedule(@PathVariable Long id,
                                 RedirectAttributes redirectAttributes) {
        scheduleService.deleteSchedule(id);
        redirectAttributes.addFlashAttribute("success", "Schedule deleted.");
        return "redirect:/schedules";
    }
}
