package com.homesync.service;

import com.homesync.adapter.ExternalSecurityService;
import com.homesync.model.Device;
import com.homesync.model.Schedule;
import com.homesync.model.User;
import com.homesync.repository.DeviceRepository;
import com.homesync.repository.ScheduleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * SERVICE layer (MVC Pattern — business logic lives here, NOT in controller).
 *
 * SOLID – SRP: This class has ONE job — handle schedule automation logic.
 *   It does NOT handle HTTP (ScheduleController does that).
 *   It does NOT handle external notifications directly (Adapter does that).
 *
 * ADAPTER PATTERN usage:
 *   ScheduleService depends on ExternalSecurityService INTERFACE (the Target).
 *   SecurityServiceAdapter implements that interface and translates our
 *   Schedule data into the format the external service understands.
 *   ScheduleService never knows which external service it is talking to.
 *
 * SOLID – DIP: Depends on ExternalSecurityService interface, not the concrete
 *   SecurityServiceAdapter class. Spring injects the adapter automatically.
 *
 * MEMBER 4 owns this class.
 */
@Service
@RequiredArgsConstructor
@Transactional
public class ScheduleService {

    private final ScheduleRepository scheduleRepository;
    private final DeviceRepository deviceRepository;

    // ADAPTER PATTERN + DIP — depends on interface, not concrete adapter class
    private final ExternalSecurityService externalSecurityService;

    // ── Major Use Case: Schedule Automation ──────────────────────────────────

    /**
     * Major Use Case: Create a new schedule for a device.
     * Saves schedule and notifies external service via Adapter.
     */
    public Schedule createSchedule(String taskName, String action,
                                   String scheduledTime, String daysOfWeek,
                                   Long deviceId, User createdBy) {
        Schedule schedule = new Schedule();
        schedule.setTaskName(taskName);
        schedule.setAction(action);
        schedule.setDaysOfWeek(daysOfWeek);
        schedule.setActive(true);
        schedule.setCreatedBy(createdBy);

        // Link device if provided
        if (deviceId != null) {
            deviceRepository.findById(deviceId).ifPresent(schedule::setDevice);
        }

        // Parse time string "HH:mm" to LocalTime
        if (scheduledTime != null && !scheduledTime.isBlank()) {
            schedule.setScheduledTime(java.time.LocalTime.parse(scheduledTime));
        }

        Schedule saved = scheduleRepository.save(schedule);

        // ADAPTER PATTERN: notify external service using the adapter
        // ScheduleService only calls the interface — it never touches the adapter directly
        externalSecurityService.notifyScheduleCreated(
                saved.getTaskName(),
                saved.getAction(),
                saved.getDaysOfWeek()
        );

        return saved;
    }

    /**
     * Minor Use Case: Undo Last Command — deactivates the most recent schedule
     * and restores the previous action (stored in previousAction field).
     */
    public String undoLastSchedule(User user) {
        List<Schedule> userSchedules = scheduleRepository.findByCreatedBy(user);
        if (userSchedules.isEmpty()) {
            return "No schedules to undo.";
        }

        // Get the most recently created schedule
        Schedule last = userSchedules.get(userSchedules.size() - 1);
        last.setActive(false);

        String undoneTask = last.getTaskName();
        scheduleRepository.save(last);

        // Notify external service about the undo via Adapter
        externalSecurityService.notifyScheduleCreated(
                "UNDO: " + undoneTask,
                last.getPreviousAction() != null ? last.getPreviousAction() : "NONE",
                last.getDaysOfWeek()
        );

        return "Undone schedule: " + undoneTask;
    }

    // ── Query methods — used by ScheduleController to populate views ──────────

    public List<Schedule> getAllSchedules() {
        return scheduleRepository.findAll();
    }

    public List<Schedule> getSchedulesByUser(User user) {
        return scheduleRepository.findByCreatedBy(user);
    }

    public List<Schedule> getActiveSchedules() {
        return scheduleRepository.findByActiveTrue();
    }

    public Schedule getScheduleById(Long id) {
        return scheduleRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Schedule not found: " + id));
    }

    /**
     * Minor Use Case: Delete a schedule (admin only).
     */
    public void deleteSchedule(Long id) {
        scheduleRepository.deleteById(id);
    }

    /**
     * Toggle a schedule active/inactive.
     */
    public Schedule toggleSchedule(Long id) {
        Schedule schedule = getScheduleById(id);
        schedule.setActive(!schedule.isActive());
        return scheduleRepository.save(schedule);
    }
}
