package com.hackathon.repository;

import com.hackathon.entity.Assignment;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AssignmentRepository extends JpaRepository<Assignment, Long> {
    long countByParticipantId(Long participantId);
}
