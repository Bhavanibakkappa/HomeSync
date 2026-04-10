package com.homesync.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * MODEL layer (MVC Pattern).
 *
 * SOLID – OCP (Open/Closed Principle):
 *   Device is the BASE class — closed for modification.
 *   New device types (SmartLight, SmartFan, SmartLock) EXTEND it
 *   without changing this class. You can add 10 more device types
 *   and this file never changes.
 *
 * MEMBER 2 owns this class.
 */
@Entity
@Table(name = "devices")
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "device_type", discriminatorType = DiscriminatorType.STRING)
@Data
@NoArgsConstructor
public abstract class Device {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private String location;   // e.g. "Living Room", "Bedroom"

    @Enumerated(EnumType.STRING)
    private DeviceStatus status = DeviceStatus.OFF;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User owner;

    public enum DeviceStatus {
        ON, OFF, ERROR
    }

    /**
     * OCP: Each subclass overrides this to perform its own action.
     * The base class defines the contract; subclasses extend behaviour.
     */
    public abstract String getDeviceType();
    public abstract void performAction(String action);
}
