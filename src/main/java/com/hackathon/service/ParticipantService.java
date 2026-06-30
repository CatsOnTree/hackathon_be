package com.hackathon.service;

import com.hackathon.dto.ParticipantRegistrationRequest;
import com.hackathon.dto.ResumeAnalysis;
import com.hackathon.entity.Participant;
import com.hackathon.entity.ParticipantStatus;
import com.hackathon.exception.ResourceNotFoundException;
import com.hackathon.repository.EventRepository;
import com.hackathon.repository.ParticipantRepository;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

@Service
public class ParticipantService {

    private static final Logger log = LoggerFactory.getLogger(ParticipantService.class);

    private final ParticipantRepository participantRepository;
    private final EventRepository eventRepository;
    private final FileStorageService fileStorageService;
    private final ResumeAnalysisService resumeAnalysisService;
    private final MockEmailService emailService;

    public ParticipantService(ParticipantRepository participantRepository, EventRepository eventRepository,
                              FileStorageService fileStorageService, ResumeAnalysisService resumeAnalysisService,
                              MockEmailService emailService) {
        this.participantRepository = participantRepository;
        this.eventRepository = eventRepository;
        this.fileStorageService = fileStorageService;
        this.resumeAnalysisService = resumeAnalysisService;
        this.emailService = emailService;
    }

    @Transactional
    public Participant register(ParticipantRegistrationRequest request, MultipartFile resume, MultipartFile photo) {
        log.info("Register phase=start email={} eventId={} resumePresent={} photoPresent={}",
                request.email(), request.eventId(), hasFile(resume), hasFile(photo));

        log.info("Register phase=event_lookup eventId={}", request.eventId());
        eventRepository.findById(request.eventId())
                .orElseThrow(() -> new ResourceNotFoundException("Event not found: " + request.eventId()));

        log.info("Register phase=resume_analysis_start email={}", request.email());
        ResumeAnalysis analysis = resumeAnalysisService.analyze(resume, request.experienceYears());
        log.info("Register phase=resume_analysis_done email={} aiScore={} skillsLength={} jsonLength={}",
                request.email(), analysis.aiScore(), length(analysis.skills()), length(analysis.structuredJson()));

        log.info("Register phase=file_storage_start email={}", request.email());
        String resumeUrl = fileStorageService.store(resume, "resumes");
        String photoUrl = fileStorageService.store(photo, "photos");
        log.info("Register phase=file_storage_done email={} resumeUrl={} photoUrl={}", request.email(), resumeUrl, photoUrl);

        String participantCode = nextParticipantCode();
        log.info("Register phase=build_entity email={} participantCode={}", request.email(), participantCode);
        Participant participant = Participant.builder()
                .participantCode(participantCode)
                .eventId(request.eventId())
                .name(request.name())
                .email(request.email())
                .phone(request.phone())
                .experienceYears(request.experienceYears())
                .resumeUrl(resumeUrl)
                .photoUrl(photoUrl)
                .skills(analysis.skills())
                .aiScore(analysis.aiScore())
                .resumeAnalysisJson(analysis.structuredJson())
                .status(ParticipantStatus.REGISTERED)
                .build();
        log.info("Register phase=db_save_start email={} participantCode={}", participant.getEmail(), participant.getParticipantCode());
        participant = participantRepository.save(participant);
        log.info("Register phase=db_save_done participantId={} participantCode={}",
                participant.getId(), participant.getParticipantCode());

        log.info("Register phase=email_start participantId={} email={}", participant.getId(), participant.getEmail());
        emailService.send(participant.getId(), participant.getEmail(), "Registration confirmed: " + participant.getParticipantCode());
        log.info("Register phase=done participantId={} email={}", participant.getId(), participant.getEmail());
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

    private boolean hasFile(MultipartFile file) {
        return file != null && !file.isEmpty();
    }

    private int length(String value) {
        return value == null ? 0 : value.length();
    }
}
