package com.hackathon.service;

import com.hackathon.dto.FeedbackRequest;
import com.hackathon.entity.Feedback;
import com.hackathon.exception.ResourceNotFoundException;
import com.hackathon.repository.FeedbackRepository;
import com.hackathon.repository.PanelistRepository;
import com.hackathon.repository.ParticipantRepository;
import java.util.List;
import org.springframework.stereotype.Service;

@Service
public class FeedbackService {

    private final FeedbackRepository feedbackRepository;
    private final ParticipantRepository participantRepository;
    private final PanelistRepository panelistRepository;

    public FeedbackService(FeedbackRepository feedbackRepository, ParticipantRepository participantRepository,
                           PanelistRepository panelistRepository) {
        this.feedbackRepository = feedbackRepository;
        this.participantRepository = participantRepository;
        this.panelistRepository = panelistRepository;
    }

    public Feedback create(FeedbackRequest request) {
        participantRepository.findById(request.participantId())
                .orElseThrow(() -> new ResourceNotFoundException("Participant not found: " + request.participantId()));
        panelistRepository.findById(request.panelistId())
                .orElseThrow(() -> new ResourceNotFoundException("Panelist not found: " + request.panelistId()));
        return feedbackRepository.save(Feedback.builder()
                .participantId(request.participantId())
                .panelistId(request.panelistId())
                .technicalRating(request.technicalRating())
                .communicationRating(request.communicationRating())
                .recommendation(request.recommendation())
                .comments(request.comments())
                .build());
    }

    public List<Feedback> findAll() {
        return feedbackRepository.findAll();
    }
}
