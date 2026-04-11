package com.homesync.adapter;

import org.springframework.stereotype.Component;

/**
 * ADAPTER PATTERN — Concrete Adapter
 *
 * This class is the BRIDGE between our HomeSync system and an
 * external third-party security/notification service.
 *
 * The 3 roles of Adapter Pattern in this file:
 *
 *   Target   = ExternalSecurityService (interface this class implements)
 *   Adaptee  = ThirdPartySecurityApi (the external system — simulated below)
 *   Adapter  = SecurityServiceAdapter (THIS class — translates between them)
 *
 * HOW IT WORKS:
 *   ScheduleService calls externalSecurityService.notifyScheduleCreated(...)
 *   → Spring routes the call to SecurityServiceAdapter (this class)
 *   → This class ADAPTS/TRANSLATES our data into the format ThirdPartySecurityApi expects
 *   → ThirdPartySecurityApi is called with its own method signature
 *
 * WHY ADAPTER HERE:
 *   ThirdPartySecurityApi uses different method names and data formats.
 *   Without the adapter, ScheduleService would have to know about the
 *   third-party's format — breaking SRP and DIP.
 *   The Adapter absorbs that translation responsibility.
 *
 * SOLID – SRP: This class has ONE job — translate our calls to external format.
 * SOLID – OCP: External API changes? Update only THIS file. Nothing else changes.
 *
 * MEMBER 4 owns this class.
 */
@Component
public class SecurityServiceAdapter implements ExternalSecurityService {

    // ── Simulated third-party API (Adaptee) ──────────────────────────────────
    // In a real project this would be a RestTemplate/WebClient or an SDK object.
    // We simulate it as a static inner class so the Adapter pattern is visible.
    private final ThirdPartySecurityApi thirdPartyApi = new ThirdPartySecurityApi();

    /**
     * ADAPTER TRANSLATION #1:
     * Our interface:    notifyScheduleCreated(taskName, action, daysOfWeek)
     * Third-party API:  postEvent(eventCode, payload)
     *
     * This method translates our domain language into the external API's format.
     */
    @Override
    public void notifyScheduleCreated(String taskName, String action, String daysOfWeek) {
        // Build the payload in the format ThirdPartySecurityApi expects
        String eventCode = "SCHEDULE_EVENT";
        String payload = String.format(
                "{\"task\":\"%s\",\"action\":\"%s\",\"days\":\"%s\",\"source\":\"HomeSync\"}",
                taskName, action, daysOfWeek != null ? daysOfWeek : "ALL"
        );

        System.out.println("[ADAPTER] Translating notifyScheduleCreated → thirdPartyApi.postEvent()");
        thirdPartyApi.postEvent(eventCode, payload);
    }

    /**
     * ADAPTER TRANSLATION #2:
     * Our interface:    sendAlert(alertType, message)
     * Third-party API:  pushNotification(priority, rawMessage)
     *
     * Maps our alert types to the external service's priority levels.
     */
    @Override
    public void sendAlert(String alertType, String message) {
        // Translate our alert type to the third-party priority level
        int priority = switch (alertType.toUpperCase()) {
            case "SCHEDULE_CONFLICT" -> 2;
            case "DEVICE_UNAVAILABLE" -> 3;
            case "CRITICAL" -> 1;
            default -> 5;
        };

        String rawMessage = "[HomeSync Alert] " + alertType + ": " + message;

        System.out.println("[ADAPTER] Translating sendAlert → thirdPartyApi.pushNotification()");
        thirdPartyApi.pushNotification(priority, rawMessage);
    }

    // ── Simulated Third-Party API (Adaptee) ───────────────────────────────────
    /**
     * This represents a third-party security service SDK with its OWN method names.
     * HomeSync cannot change these method signatures — hence the Adapter.
     * In production this would be an external library or REST client.
     */
    private static class ThirdPartySecurityApi {

        /** Third-party method — posts a structured event to their system. */
        public void postEvent(String eventCode, String jsonPayload) {
            System.out.println("================================================");
            System.out.println("[THIRD-PARTY API] postEvent() called");
            System.out.println("[THIRD-PARTY API] Event Code : " + eventCode);
            System.out.println("[THIRD-PARTY API] Payload    : " + jsonPayload);
            System.out.println("[THIRD-PARTY API] Status     : 200 OK (simulated)");
            System.out.println("================================================");
        }

        /** Third-party method — pushes a priority notification to their system. */
        public void pushNotification(int priority, String rawMessage) {
            System.out.println("================================================");
            System.out.println("[THIRD-PARTY API] pushNotification() called");
            System.out.println("[THIRD-PARTY API] Priority   : " + priority);
            System.out.println("[THIRD-PARTY API] Message    : " + rawMessage);
            System.out.println("[THIRD-PARTY API] Status     : ACK (simulated)");
            System.out.println("================================================");
        }
    }
}
