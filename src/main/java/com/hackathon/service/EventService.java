package com.hackathon.service;

import com.hackathon.dto.EventRequest;
import com.hackathon.entity.Event;
import com.hackathon.entity.EventStatus;
import com.hackathon.exception.ResourceNotFoundException;
import com.hackathon.repository.EventRepository;
import java.util.List;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class EventService {

    private final EventRepository eventRepository;
    private final QrCodeService qrCodeService;
    private final String baseUrl;

    public EventService(EventRepository eventRepository, QrCodeService qrCodeService,
                        @Value("${app.base-qr-url}") String baseUrl) {
        this.eventRepository = eventRepository;
        this.qrCodeService = qrCodeService;
        this.baseUrl = baseUrl;
    }

    @Transactional
    public Event create(EventRequest request) {
        Event event = Event.builder()
                .name(request.name())
                .description(request.description())
                .eventDate(request.eventDate())
                .status(request.status() == null ? EventStatus.OPEN : request.status())
                .build();
        event = eventRepository.save(event);
        String registrationUrl = baseUrl + "/participants/register?eventId=" + event.getId();
        event.setRegistrationUrl(registrationUrl);
        event.setQrCodeUrl(qrCodeService.generateQrCode(registrationUrl));
        return eventRepository.save(event);
    }

    public List<Event> findAll() {
        return eventRepository.findAll();
    }

    public Event findById(Long id) {
        return eventRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Event not found: " + id));
    }
}
