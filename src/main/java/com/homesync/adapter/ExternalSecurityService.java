package com.homesync.adapter;

/**
 * ADAPTER PATTERN — Target Interface
 *
 * This is what OUR code (ScheduleService) knows and depends on.
 * ScheduleService calls methods on THIS interface only.
 *
 * The Adapter Pattern has 3 roles:
 *   Target   = ExternalSecurityService (this interface — what our code expects)
 *   Adaptee  = The external/third-party system being wrapped (simulated here)
 *   Adapter  = SecurityServiceAdapter (translates our calls to the external format)
 *
 * SOLID – DIP: ScheduleService depends on THIS interface (high-level abstraction),
 *   NOT on SecurityServiceAdapter (low-level concrete class).
 *   Spring injects SecurityServiceAdapter wherever this interface is needed.
 *
 * SOLID – OCP: If the external security service changes its API, we ONLY update
 *   SecurityServiceAdapter. ScheduleService and this interface remain unchanged.
 *
 * MEMBER 4 owns this interface.
 */
public interface ExternalSecurityService {

    /**
     * Notifies the external service that a new schedule has been created.
     *
     * @param taskName    name of the scheduled task
     * @param action      what action the schedule performs (ON/OFF etc.)
     * @param daysOfWeek  which days the schedule runs
     */
    void notifyScheduleCreated(String taskName, String action, String daysOfWeek);

    /**
     * Sends an alert to the external service with a custom message.
     *
     * @param alertType   category of alert (e.g. "SCHEDULE_CONFLICT", "DEVICE_UNAVAILABLE")
     * @param message     human-readable message describing the event
     */
    void sendAlert(String alertType, String message);
}
