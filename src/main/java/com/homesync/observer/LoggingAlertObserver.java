package com.homesync.observer;

import com.homesync.model.SecurityLog;
import com.homesync.repository.SecurityLogRepository;
import org.springframework.stereotype.Component;

/**
 * OBSERVER PATTERN — Concrete Observer #1
 *
 * SOLID – SRP: This class has ONE job — save security events to the database.
 * SOLID – DIP: Implements AlertObserver interface.
 *              SecurityService never references this class directly.
 *
 * When SecurityService calls notifyObservers(), this fires automatically
 * because Spring injects ALL AlertObserver beans into SecurityService.
 *
 * MEMBER 3 owns this class.
 */
@Component
public class LoggingAlertObserver implements AlertObserver {

    private final SecurityLogRepository securityLogRepository;

    public LoggingAlertObserver(SecurityLogRepository securityLogRepository) {
        this.securityLogRepository = securityLogRepository;
    }

    /**
     * Called by SecurityService.notifyObservers() for every security event.
     * Persists the SecurityLog entity to MySQL via JPA.
     */
    @Override
    public void onAlert(SecurityLog log) {
        securityLogRepository.save(log);
        System.out.println("[LOG] Security event saved to DB: " + log.getState()
                + " | " + log.getDescription());
    }
}
