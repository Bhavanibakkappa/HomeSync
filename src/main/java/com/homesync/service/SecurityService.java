package com.homesync.service;

import com.homesync.model.SecurityLog;
import com.homesync.model.SecurityLog.SecurityState;
import com.homesync.observer.AlertObserver;
import com.homesync.repository.SecurityLogRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

/**
 * SERVICE layer (MVC Pattern — business logic lives here, NOT in controller).
 *
 * SOLID – SRP: This class has ONE job — handle security system logic.
 *   It does NOT handle HTTP (SecurityController does that).
 *   It does NOT save to DB directly (LoggingAlertObserver does that via observer).
 *
 * SOLID – DIP: Depends on List<AlertObserver> INTERFACE.
 *   Spring auto-injects ALL beans implementing AlertObserver:
 *   → LoggingAlertObserver (saves to DB)
 *   → ExternalServiceAlertObserver (notifies external service)
 *   SecurityService never imports or references these classes directly.
 *
 * OBSERVER PATTERN:
 *   Subject   = SecurityService (holds the observer list, calls notifyObservers)
 *   Observers = LoggingAlertObserver, ExternalServiceAlertObserver
 *   Event     = SecurityLog (the data passed to every observer on fire)
 *
 * MEMBER 3 owns this class.
 */
@Service
public class SecurityService {

    private final SecurityLogRepository securityLogRepository;

    // DIP — depends on interface, not concrete classes
    // Spring injects BOTH LoggingAlertObserver and ExternalServiceAlertObserver here
    private final List<AlertObserver> observers;

    public SecurityService(SecurityLogRepository securityLogRepository,
                           List<AlertObserver> observers) {
        this.securityLogRepository = securityLogRepository;
        this.observers = observers;
    }

    // ── Core method — builds the log and fires all observers ──────────────────

    /**
     * Creates a SecurityLog for the given state and notifies all observers.
     * This is the heart of the Observer pattern in this project.
     */
    public void triggerAlert(SecurityState state, String description) {
        SecurityLog log = new SecurityLog();
        log.setState(state);
        log.setDescription(description);
        log.setTimestamp(LocalDateTime.now());

        // OBSERVER PATTERN — notify ALL registered observers
        notifyObservers(log);
    }

    /**
     * Iterates the observer list and calls onAlert() on each.
     * Neither SecurityService nor this method knows HOW each observer reacts.
     * That is the power of the Observer + DIP combination.
     */
    private void notifyObservers(SecurityLog log) {
        for (AlertObserver observer : observers) {
            observer.onAlert(log);
        }
    }

    // ── Use Case methods — called by SecurityController ───────────────────────

    /** Major Use Case: Arm the security system at a given location. */
    public void armSystem(String location) {
        triggerAlert(SecurityState.ARMED, "System armed at: " + location);
    }

    /** Major Use Case: Disarm the security system at a given location. */
    public void disarmSystem(String location) {
        triggerAlert(SecurityState.DISARMED, "System disarmed at: " + location);
    }

    /** Major Use Case: Trigger an intrusion alert (motion/sensor detected). */
    public void triggerIntrusion(String location) {
        triggerAlert(SecurityState.INTRUSION_DETECTED, "Motion detected at: " + location);
    }

    /** Minor Use Case: Reset the security system back to DISARMED. */
    public void resetSystem(String location) {
        triggerAlert(SecurityState.RESET, "System reset at: " + location);
    }

    // ── Query methods — used by SecurityController to populate views ──────────

    public List<SecurityLog> getAllLogs() {
        return securityLogRepository.findAllByOrderByTimestampDesc();
    }

    /**
     * Returns the most recent SecurityLog — used to display current system state
     * on the security dashboard (ARMED / DISARMED / INTRUSION etc.)
     */
    public SecurityLog getCurrentState() {
        return securityLogRepository.findTopByOrderByTimestampDesc().orElse(null);
    }

    public void deleteLog(Long id) {
        securityLogRepository.deleteById(id);
    }
}
