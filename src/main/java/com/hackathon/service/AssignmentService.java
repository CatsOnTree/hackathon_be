package com.hackathon.service;

import com.hackathon.dto.AssignmentRequest;
import com.hackathon.entity.Assignment;
import com.hackathon.entity.Participant;
import com.hackathon.entity.ParticipantStatus;
import com.hackathon.exception.ResourceNotFoundException;
import com.hackathon.repository.AssignmentRepository;
import com.hackathon.repository.PanelistRepository;
import com.hackathon.repository.ParticipantRepository;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AssignmentService {

    private final AssignmentRepository assignmentRepository;
    private final ParticipantRepository participantRepository;
    private final PanelistRepository panelistRepository;

    public AssignmentService(AssignmentRepository assignmentRepository, ParticipantRepository participantRepository,
                             PanelistRepository panelistRepository) {
        this.assignmentRepository = assignmentRepository;
        this.participantRepository = participantRepository;
        this.panelistRepository = panelistRepository;
    }

    @Transactional
    public Assignment create(AssignmentRequest request) {
        Participant participant = participantRepository.findById(request.participantId())
                .orElseThrow(() -> new ResourceNotFoundException("Participant not found: " + request.participantId()));
        panelistRepository.findById(request.panelistId())
                .orElseThrow(() -> new ResourceNotFoundException("Panelist not found: " + request.panelistId()));
        participant.setStatus(ParticipantStatus.ASSIGNED);
        participantRepository.save(participant);
        return assignmentRepository.save(Assignment.builder()
                .participantId(request.participantId())
                .panelistId(request.panelistId())
                .build());
    }

    public List<Assignment> findAll() {
        return assignmentRepository.findAll();
    }
}
