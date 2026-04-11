package com.homesync.observer;

import com.homesync.model.SecurityLog;

/**
 * OBSERVER PATTERN — Observer Interface
 *
 * SOLID – DIP (Dependency Inversion Principle):
 *   SecurityService depends on THIS interface, not on any
 *   concrete class like LoggingAlertObserver or ExternalServiceAlertObserver.
 *   High-level module (SecurityService) never touches low-level modules directly.
 *
 * MEMBER 3 owns this interface.
 */
public interface AlertObserver {
    void onAlert(SecurityLog log);
}
