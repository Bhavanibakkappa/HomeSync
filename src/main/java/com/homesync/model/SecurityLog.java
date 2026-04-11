package com.homesync.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

/**
 * MODEL layer (MVC Pattern).
 *
 * SOLID – SRP: This class has ONE job — represent a security event record.
 *   It does NOT contain security logic (that lives in SecurityService).
 *
 * SOLID – DIP: SecurityService depends on AlertObserver interface.
 *   This model is the data carrier passed between layers.
 *
 * MEMBER 3 owns this class.
 */
@Entity
@Table(name = "security_logs")
@Data
@NoArgsConstructor
public class SecurityLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private SecurityState state;

    @Column(nullable = false)
    private LocalDateTime timestamp = LocalDateTime.now();

    private String description;

    @ManyToOne
    @JoinColumn(name = "triggered_by")
    private User triggeredBy;

    /**
     * States model the Security System State Diagram:
     * DISARMED → ARMED → INTRUSION_DETECTED → ALARM_ACTIVE → ALERT_SENT → RESET
     */
    public enum SecurityState {
        DISARMED,
        ARMED,
        INTRUSION_DETECTED,
        ALARM_ACTIVE,
        ALERT_SENT,
        RESET
    }
}
