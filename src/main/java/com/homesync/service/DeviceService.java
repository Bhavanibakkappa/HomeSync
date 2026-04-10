package com.homesync.service;

import com.homesync.factory.DeviceFactory;
import com.homesync.model.Device;
import com.homesync.model.User;
import com.homesync.repository.DeviceRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * SERVICE layer (MVC Pattern).
 *
 * ╔══════════════════════════════════════════════════════╗
 * ║  SOLID – OCP (Open/Closed Principle)                ║
 * ║  + FACTORY Pattern usage                            ║
 * ║  Member 2 – Device Configuration                   ║
 * ╚══════════════════════════════════════════════════════╝
 *
 * OCP in this service:
 *   addDevice() calls DeviceFactory.create() — it never does
 *   "if SMART_LIGHT else if SMART_FAN" itself. Adding a new
 *   device type requires ZERO changes to this service.
 *
 * MEMBER 2 owns this class.
 */
@Service
@RequiredArgsConstructor
@Transactional
public class DeviceService {

    private final DeviceRepository deviceRepository;
    private final DeviceFactory deviceFactory;   // Factory Pattern injected

    /**
     * Major Use Case: Add a new device.
     * Uses DeviceFactory — OCP: this method never changes
     * when a new device type is added.
     */
    public Device addDevice(String type, String name,
                            String location, User owner) {
        // FACTORY PATTERN: delegate creation to factory
        Device device = deviceFactory.create(type, name, location);
        device.setOwner(owner);
        return deviceRepository.save(device);
    }

    /**
     * Remove a device by ID.
     */
    public void removeDevice(Long deviceId) {
        Device device = getDeviceById(deviceId);
        deviceRepository.delete(device);
    }

    /**
     * Modify a device's name or location.
     */
    public Device modifyDevice(Long deviceId, String newName, String newLocation) {
        Device device = getDeviceById(deviceId);
        if (newName != null && !newName.isBlank()) {
            device.setName(newName);
        }
        if (newLocation != null && !newLocation.isBlank()) {
            device.setLocation(newLocation);
        }
        return deviceRepository.save(device);
    }

    /**
     * Minor Use Case: View real-time device status.
     */
    public List<Device> getDevicesByOwner(User owner) {
        return deviceRepository.findByOwner(owner);
    }

    public List<Device> getAllDevices() {
        return deviceRepository.findAll();
    }

    /**
     * Toggle device ON/OFF.
     */
    public Device toggleDevice(Long deviceId, String action) {
        Device device = getDeviceById(deviceId);
        device.performAction(action);   // OCP: polymorphism — each subclass handles its own action
        return deviceRepository.save(device);
    }

    public Device getDeviceById(Long id) {
        return deviceRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Device not found: " + id));
    }
}