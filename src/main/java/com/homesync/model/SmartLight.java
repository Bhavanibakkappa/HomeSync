package com.homesync.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

/**
 * SOLID – OCP: SmartLight EXTENDS Device without modifying it.
 * Factory Pattern: DeviceFactory creates this class by type string.
 */
@Entity
@DiscriminatorValue("SMART_LIGHT")
@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
public class SmartLight extends Device {

    private int brightness = 100;   // 0–100
    private String colorHex = "#FFFFFF";

    @Override
    public String getDeviceType() {
        return "SMART_LIGHT";
    }

    @Override
    public void performAction(String action) {
        switch (action.toUpperCase()) {
            case "ON"  -> setStatus(DeviceStatus.ON);
            case "OFF" -> setStatus(DeviceStatus.OFF);
            default    -> System.out.println("SmartLight: unknown action " + action);
        }
    }
}
