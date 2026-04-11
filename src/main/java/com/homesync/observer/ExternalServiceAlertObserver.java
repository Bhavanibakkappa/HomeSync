package com.homesync.observer;

import com.homesync.model.SecurityLog;
import org.springframework.stereotype.Component;

/**
 * OBSERVER PATTERN — Concrete Observer #2
 *
 * SOLID – SRP: This class has ONE job — notify an external security service.
 *              It does NOT save to DB (that's LoggingAlertObserver's job).
 * SOLID – DIP: Implements AlertObserver interface.
 *              SecurityService never references this class directly.
 *
 * GRASP – Low Coupling:
 *   SecurityService holds List<AlertObserver>.
 *   It never imports or calls ExternalServiceAlertObserver directly.
 *   Spring injects this bean automatically alongside LoggingAlertObserver.
 *   Adding or removing this observer needs ZERO changes to SecurityService.
 *
 * In a real deployment, sendExternalNotification() would make
 * an HTTP POST to a security company's REST API or send an SMS.
 * Here it is simulated via console output for demonstration.
 *
 * MEMBER 3 owns this class.
 */
@Component
public class ExternalServiceAlertObserver implements AlertObserver {

    private static final String EXTERNAL_API_URL = "https://external-security-service.com/api/alert";

    /**
     * Called by SecurityService.notifyObservers() for every security event.
     * Only escalates serious states to the external service.
     */
    @Override
    public void onAlert(SecurityLog log) {
        if (shouldNotifyExternal(log.getState())) {
            sendExternalNotification(log);
        } else {
            System.out.println("[EXTERNAL] State " + log.getState()
                    + " — no external notification required.");
        }
    }

    /**
     * Only INTRUSION_DETECTED, ALARM_ACTIVE and ALERT_SENT
     * are serious enough to contact the external service.
     */
    private boolean shouldNotifyExternal(SecurityLog.SecurityState state) {
        return state == SecurityLog.SecurityState.INTRUSION_DETECTED
                || state == SecurityLog.SecurityState.ALARM_ACTIVE
                || state == SecurityLog.SecurityState.ALERT_SENT;
    }

    /**
     * Simulates an HTTP POST to an external security company.
     * Replace with RestTemplate/WebClient call in production.
     */
    private void sendExternalNotification(SecurityLog log) {
        System.out.println("================================================");
        System.out.println("[EXTERNAL ALERT] Contacting external security service");
        System.out.println("[EXTERNAL ALERT] Endpoint   : " + EXTERNAL_API_URL);
        System.out.println("[EXTERNAL ALERT] Event State: " + log.getState());
        System.out.println("[EXTERNAL ALERT] Description: " + log.getDescription());
        System.out.println("[EXTERNAL ALERT] Timestamp  : " + log.getTimestamp());
        System.out.println("[EXTERNAL ALERT] HTTP Status: 200 OK (simulated)");
        System.out.println("================================================");
    }
}
