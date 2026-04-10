package com.homesync.repository;

import com.homesync.model.Device;
import com.homesync.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface DeviceRepository extends JpaRepository<Device, Long> {
    List<Device> findByOwner(User owner);
    List<Device> findByStatus(Device.DeviceStatus status);
    List<Device> findByLocation(String location);
}
