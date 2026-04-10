package com.homesync.repository;

import com.homesync.model.SecurityLog;
import com.homesync.model.SecurityLog.SecurityState;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface SecurityLogRepository extends JpaRepository<SecurityLog, Long> {
    List<SecurityLog> findAllByOrderByTimestampDesc();
    Optional<SecurityLog> findTopByOrderByTimestampDesc();
    List<SecurityLog> findByState(SecurityState state);
}
