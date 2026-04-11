package com.homesync.repository;

import com.homesync.model.SecurityLog;
import com.homesync.model.SecurityLog.SecurityState;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

/**
 * REPOSITORY — Model layer (MVC Pattern).
 * Spring Data JPA auto-implements all CRUD — no SQL needed.
 *
 * SOLID – DIP: SecurityService depends on this INTERFACE,
 *   not on any concrete JPA implementation class.
 *
 * MEMBER 3 owns this interface.
 */
@Repository
public interface SecurityLogRepository extends JpaRepository<SecurityLog, Long> {

    // Returns all logs newest-first — used by SecurityController
    List<SecurityLog> findAllByOrderByTimestampDesc();

    // Returns the single most recent log — used to show current system state
    Optional<SecurityLog> findTopByOrderByTimestampDesc();

    // Filter logs by a specific security state
    List<SecurityLog> findByState(SecurityState state);
}
