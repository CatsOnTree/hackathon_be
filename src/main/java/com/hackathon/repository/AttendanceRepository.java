package com.hackathon.repository;

import com.hackathon.entity.Attendance;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AttendanceRepository extends JpaRepository<Attendance, Long> {
    Optional<Attendance> findByParticipantId(Long participantId);
}
