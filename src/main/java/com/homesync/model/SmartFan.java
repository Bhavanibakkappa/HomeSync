package com.homesync.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

/**
 * SOLID – OCP: SmartFan EXTENDS Device without modifying it.
 */
@Entity
@DiscriminatorValue("SMART_FAN")
@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
public class SmartFan extends Device {

    private int speed = 1;   // 1–5

    @Override
    public String getDeviceType() {
        return "SMART_FAN";
    }

    @Override
    public void performAction(String action) {
        switch (action.toUpperCase()) {
            case "ON"       -> setStatus(DeviceStatus.ON);
            case "OFF"      -> setStatus(DeviceStatus.OFF);
            case "SPEED_UP" -> speed = Math.min(speed + 1, 5);
            case "SPEED_DN" -> speed = Math.max(speed - 1, 1);
            default         -> System.out.println("SmartFan: unknown action " + action);
        }
    }
}
