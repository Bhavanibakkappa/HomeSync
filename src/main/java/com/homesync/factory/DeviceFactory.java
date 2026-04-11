package com.homesync.factory;

import com.homesync.model.Device;
import com.homesync.model.SmartFan;
import com.homesync.model.SmartLight;
import org.springframework.stereotype.Component;

/**
 * ╔══════════════════════════════════════════════════════╗
 * ║  DESIGN PATTERN: FACTORY (Creational)               ║
 * ║  Member 2 – Device Configuration                    ║
 * ╚══════════════════════════════════════════════════════╝
 *
 * WHY: The controller should not know HOW to create a Device.
 *      It only needs to say "give me a device of type X".
 *      DeviceFactory encapsulates all creation logic.
 *
 * HOW IT WORKS:
 *   DeviceController calls DeviceFactory.create("SMART_LIGHT")
 *   Factory returns a fully constructed SmartLight object.
 *   If we add SmartLock tomorrow, we only update THIS file.
 *
 * SOLID – OCP connection:
 *   New device type? Add a new case here. Zero changes elsewhere.
 */
@Component
public class DeviceFactory {

    /**
     * Creates the correct Device subclass based on the type string.
     *
     * @param type  device type string, e.g. "SMART_LIGHT", "SMART_FAN"
     * @param name  display name for the device
     * @param location room name, e.g. "Living Room"
     * @return a fully initialised Device subclass ready to save
     * @throws IllegalArgumentException if the type is unknown
     */
    public Device create(String type, String name, String location) {
        Device device = switch (type.toUpperCase()) {
            case "SMART_LIGHT" -> new SmartLight();
            case "SMART_FAN"   -> new SmartFan();
            // Add SmartLock, SmartAC, SmartCamera here in future
            default -> throw new IllegalArgumentException(
                    "Unknown device type: " + type +
                    ". Supported: SMART_LIGHT, SMART_FAN");
        };
        device.setName(name);
        device.setLocation(location);
        device.setStatus(Device.DeviceStatus.OFF);
        return device;
    }
}