package com.homesync.repository;

import com.homesync.model.Schedule;
import com.homesync.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface ScheduleRepository extends JpaRepository<Schedule, Long> {
    List<Schedule> findByCreatedBy(User user);
    List<Schedule> findByActiveTrue();
}
