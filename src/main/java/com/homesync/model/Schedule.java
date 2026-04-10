package com.homesync.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalTime;
import java.time.LocalDateTime;

/**
 * MODEL layer (MVC Pattern).
 * MEMBER 4 owns this class.
 */
@Entity
@Table(name = "schedules")
@Data
@NoArgsConstructor
public class Schedule {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String taskName;        // e.g. "Lights off"

    @Column(nullable = false)
    private String action;          // e.g. "OFF"

    private LocalTime scheduledTime;

    private String daysOfWeek;      // e.g. "MON,TUE,WED"

    private boolean active = true;

    @ManyToOne
    @JoinColumn(name = "device_id")
    private Device device;

    @ManyToOne
    @JoinColumn(name = "created_by")
    private User createdBy;

    private LocalDateTime createdAt = LocalDateTime.now();

    // For undo: stores the previous action before this schedule ran
    private String previousAction;
}
