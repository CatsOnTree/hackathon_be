package com.hackathon.service;

import com.hackathon.dto.ParticipantRegistrationRequest;
import com.hackathon.dto.ResumeAnalysis;
import com.hackathon.entity.Participant;
import com.hackathon.entity.ParticipantStatus;
import com.hackathon.exception.ResourceNotFoundException;
import com.hackathon.repository.EventRepository;
import com.hackathon.repository.ParticipantRepository;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

@Service
public class ParticipantService {

    private final ParticipantRepository participantRepository;
    private final EventRepository eventRepository;
    private final FileStorageService fileStorageService;
    private final MockResumeAnalysisService resumeAnalysisService;
    private final MockEmailService emailService;

    public ParticipantService(ParticipantRepository participantRepository, EventRepository eventRepository,
                              FileStorageService fileStorageService, MockResumeAnalysisService resumeAnalysisService,
                              MockEmailService emailService) {
        this.participantRepository = participantRepository;
        this.eventRepository = eventRepository;
        this.fileStorageService = fileStorageService;
        this.resumeAnalysisService = resumeAnalysisService;
        this.emailService = emailService;
    }

    @Transactional
    public Participant register(ParticipantRegistrationRequest request, MultipartFile resume, MultipartFile photo) {
        eventRepository.findById(request.eventId())
                .orElseThrow(() -> new ResourceNotFoundException("Event not found: " + request.eventId()));

        ResumeAnalysis analysis = resumeAnalysisService.analyze(resume, request.experienceYears());
        Participant participant = Participant.builder()
                .participantCode(nextParticipantCode())
                .eventId(request.eventId())
                .name(request.name())
                .email(request.email())
                .phone(request.phone())
                .experienceYears(request.experienceYears())
                .resumeUrl(fileStorageService.store(resume, "resumes"))
                .photoUrl(fileStorageService.store(photo, "photos"))
                .skills(analysis.skills())
                .aiScore(analysis.aiScore())
                .status(ParticipantStatus.REGISTERED)
                .build();
        participant = participantRepository.save(participant);
        emailService.send(participant.getId(), participant.getEmail(), "Registration confirmed: " + participant.getParticipantCode());
        return participant;
    }

    public List<Participant> findAll() {
        return participantRepository.findAll();
    }

    public List<Participant> findByEvent(Long eventId) {
        return participantRepository.findByEventId(eventId);
    }

    public Participant findById(Long id) {
        return participantRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Participant not found: " + id));
    }

    private synchronized String nextParticipantCode() {
        long next = participantRepository.findTopByOrderByIdDesc()
                .map(participant -> participant.getId() + 1)
                .orElse(1L);
        return "PART-%04d".formatted(next);
    }
}
