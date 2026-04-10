package com.homesync.controller;

import com.homesync.service.DeviceService;
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
 * ║  MVC – Controller                                    ║
 * ║  GRASP – Controller (handles device system events)   ║
 * ║  Member 2 – Device Configuration                     ║
 * ╚═══════════════════════════════════════════════════════╝
 *
 * MEMBER 2 owns this class.
 * Factory Pattern is used INSIDE DeviceService.addDevice() —
 * this controller simply calls the service, keeping itself thin.
 */
@Controller
@RequestMapping("/devices")
@RequiredArgsConstructor
public class DeviceController {

    private final DeviceService deviceService;
    private final UserService userService;

    /** Major Use Case: View all devices (dashboard) */
    @GetMapping
    public String listDevices(@AuthenticationPrincipal UserDetails userDetails, Model model) {
        userService.findByUsername(userDetails.getUsername()).ifPresent(user -> {
            model.addAttribute("devices", deviceService.getDevicesByOwner(user));
            model.addAttribute("currentUser", user);
        });
        model.addAttribute("deviceTypes", new String[]{"SMART_LIGHT", "SMART_FAN"});
        return "devices/list";   // → templates/devices/list.html
    }

    /** Show add device form */
    @GetMapping("/add")
    public String showAddForm(Model model) {
        model.addAttribute("deviceTypes", new String[]{"SMART_LIGHT", "SMART_FAN"});
        return "devices/add";
    }

    /** Major Use Case: Add device — delegates to DeviceService which uses DeviceFactory */
    @PostMapping("/add")
    public String addDevice(
            @RequestParam String type,
            @RequestParam String name,
            @RequestParam String location,
            @AuthenticationPrincipal UserDetails userDetails,
            RedirectAttributes redirectAttrs) {
        try {
            userService.findByUsername(userDetails.getUsername()).ifPresent(user ->
                deviceService.addDevice(type, name, location, user)
            );
            redirectAttrs.addFlashAttribute("success", "Device added successfully.");
        } catch (Exception e) {
            redirectAttrs.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/devices";
    }

    /** Major Use Case: Remove device */
    @PostMapping("/{id}/remove")
    public String removeDevice(@PathVariable Long id, RedirectAttributes redirectAttrs) {
        deviceService.removeDevice(id);
        redirectAttrs.addFlashAttribute("success", "Device removed.");
        return "redirect:/devices";
    }

    /** Major Use Case: Modify device */
    @PostMapping("/{id}/modify")
    public String modifyDevice(
            @PathVariable Long id,
            @RequestParam(required = false) String name,
            @RequestParam(required = false) String location,
            RedirectAttributes redirectAttrs) {
        deviceService.modifyDevice(id, name, location);
        redirectAttrs.addFlashAttribute("success", "Device updated.");
        return "redirect:/devices";
    }

    /** Minor Use Case: Toggle device ON/OFF (real-time status change) */
    @PostMapping("/{id}/toggle")
    public String toggleDevice(
            @PathVariable Long id,
            @RequestParam String action,
            RedirectAttributes redirectAttrs) {
        deviceService.toggleDevice(id, action);
        redirectAttrs.addFlashAttribute("success", "Device " + action + " executed.");
        return "redirect:/devices";
    }

    /** Minor Use Case: View device status detail */
    @GetMapping("/{id}/status")
    public String viewDeviceStatus(@PathVariable Long id, Model model) {
        model.addAttribute("device", deviceService.getDeviceById(id));
        return "devices/status";
    }
}