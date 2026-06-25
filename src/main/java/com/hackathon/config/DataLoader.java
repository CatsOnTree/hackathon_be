package com.hackathon.config;

import com.hackathon.entity.Assignment;
import com.hackathon.entity.Attendance;
import com.hackathon.entity.EmailLog;
import com.hackathon.entity.Event;
import com.hackathon.entity.EventStatus;
import com.hackathon.entity.Feedback;
import com.hackathon.entity.Panelist;
import com.hackathon.entity.Participant;
import com.hackathon.entity.ParticipantStatus;
import com.hackathon.entity.Recommendation;
import com.hackathon.entity.Squad;
import com.hackathon.entity.SquadMember;
import com.hackathon.repository.AssignmentRepository;
import com.hackathon.repository.AttendanceRepository;
import com.hackathon.repository.EmailLogRepository;
import com.hackathon.repository.EventRepository;
import com.hackathon.repository.FeedbackRepository;
import com.hackathon.repository.PanelistRepository;
import com.hackathon.repository.ParticipantRepository;
import com.hackathon.repository.SquadMemberRepository;
import com.hackathon.repository.SquadRepository;
import com.hackathon.service.QrCodeService;
import java.time.LocalDate;
import java.time.LocalDateTime;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class DataLoader implements CommandLineRunner {

    private final EventRepository eventRepository;
    private final ParticipantRepository participantRepository;
    private final PanelistRepository panelistRepository;
    private final AssignmentRepository assignmentRepository;
    private final FeedbackRepository feedbackRepository;
    private final SquadRepository squadRepository;
    private final SquadMemberRepository squadMemberRepository;
    private final AttendanceRepository attendanceRepository;
    private final EmailLogRepository emailLogRepository;
    private final QrCodeService qrCodeService;
    private final String baseUrl;

    public DataLoader(EventRepository eventRepository, ParticipantRepository participantRepository,
                      PanelistRepository panelistRepository, AssignmentRepository assignmentRepository,
                      FeedbackRepository feedbackRepository, SquadRepository squadRepository,
                      SquadMemberRepository squadMemberRepository, AttendanceRepository attendanceRepository,
                      EmailLogRepository emailLogRepository, QrCodeService qrCodeService,
                      @Value("${app.base-url}") String baseUrl) {
        this.eventRepository = eventRepository;
        this.participantRepository = participantRepository;
        this.panelistRepository = panelistRepository;
        this.assignmentRepository = assignmentRepository;
        this.feedbackRepository = feedbackRepository;
        this.squadRepository = squadRepository;
        this.squadMemberRepository = squadMemberRepository;
        this.attendanceRepository = attendanceRepository;
        this.emailLogRepository = emailLogRepository;
        this.qrCodeService = qrCodeService;
        this.baseUrl = baseUrl;
    }

    @Override
    public void run(String... args) {
        if (eventRepository.count() > 0) {
            return;
        }

        Event event = eventRepository.save(Event.builder()
                .name("AI Hiring Drive 2026")
                .description("Campus and lateral recruitment event with AI-assisted screening.")
                .eventDate(LocalDate.now().plusDays(7))
                .status(EventStatus.OPEN)
                .build());
        String registrationUrl = baseUrl + "/api/participants/register?eventId=" + event.getId();
        event.setRegistrationUrl(registrationUrl);
        event.setQrCodeUrl(qrCodeService.generateQrCode(registrationUrl));
        event = eventRepository.save(event);

        Participant participant = participantRepository.save(Participant.builder()
                .participantCode("PART-0001")
                .eventId(event.getId())
                .name("Aarav Sharma")
                .email("aarav.sharma@example.com")
                .phone("9876543210")
                .experienceYears(3)
                .resumeUrl("/uploads/demo/resume-aarav.pdf")
                .photoUrl("/uploads/demo/aarav.jpg")
                .aiScore(84)
                .skills("Java, Spring Boot, PostgreSQL, REST APIs, Docker")
                .status(ParticipantStatus.ASSIGNED)
                .build());

        Panelist panelist = panelistRepository.save(Panelist.builder()
                .name("Priya Menon")
                .email("priya.menon@example.com")
                .domain("Backend Engineering")
                .build());

        assignmentRepository.save(Assignment.builder()
                .participantId(participant.getId())
                .panelistId(panelist.getId())
                .build());

        feedbackRepository.save(Feedback.builder()
                .participantId(participant.getId())
                .panelistId(panelist.getId())
                .technicalRating(4)
                .communicationRating(5)
                .recommendation(Recommendation.HIRE)
                .comments("Strong fundamentals and clear project explanation.")
                .build());

        Squad squad = squadRepository.save(Squad.builder()
                .eventId(event.getId())
                .name("Backend Squad")
                .build());

        squadMemberRepository.save(SquadMember.builder()
                .squadId(squad.getId())
                .participantId(participant.getId())
                .build());

        attendanceRepository.save(Attendance.builder()
                .participantId(participant.getId())
                .checkinTime(LocalDateTime.now())
                .build());

        emailLogRepository.save(EmailLog.builder()
                .participantId(participant.getId())
                .email(participant.getEmail())
                .subject("Registration confirmed: " + participant.getParticipantCode())
                .sentTime(LocalDateTime.now())
                .build());
    }
}
