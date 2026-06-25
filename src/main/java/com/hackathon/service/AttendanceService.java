package com.hackathon.service;

import com.hackathon.entity.Attendance;
import com.hackathon.entity.Participant;
import com.hackathon.entity.ParticipantStatus;
import com.hackathon.exception.BadRequestException;
import com.hackathon.exception.ResourceNotFoundException;
import com.hackathon.repository.AttendanceRepository;
import com.hackathon.repository.ParticipantRepository;
import java.time.LocalDateTime;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AttendanceService {

    private final AttendanceRepository attendanceRepository;
    private final ParticipantRepository participantRepository;

    public AttendanceService(AttendanceRepository attendanceRepository, ParticipantRepository participantRepository) {
        this.attendanceRepository = attendanceRepository;
        this.participantRepository = participantRepository;
    }

    @Transactional
    public Attendance checkIn(String participantCode) {
        Participant participant = participantRepository.findByParticipantCode(participantCode)
                .orElseThrow(() -> new ResourceNotFoundException("Participant code not found: " + participantCode));
        attendanceRepository.findByParticipantId(participant.getId()).ifPresent(attendance -> {
            throw new BadRequestException("Participant already checked in");
        });
        participant.setStatus(ParticipantStatus.CHECKED_IN);
        participantRepository.save(participant);
        return attendanceRepository.save(Attendance.builder()
                .participantId(participant.getId())
                .checkinTime(LocalDateTime.now())
                .build());
    }
}
