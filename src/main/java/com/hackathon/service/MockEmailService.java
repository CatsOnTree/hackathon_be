package com.hackathon.service;

import com.hackathon.entity.EmailLog;
import com.hackathon.repository.EmailLogRepository;
import java.time.LocalDateTime;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class MockEmailService {

    private final EmailLogRepository emailLogRepository;
    private final JavaMailSender mailSender;

    public MockEmailService(EmailLogRepository emailLogRepository, ObjectProvider<JavaMailSender> mailSenderProvider) {
        this.emailLogRepository = emailLogRepository;
        this.mailSender = mailSenderProvider.getIfAvailable();
    }

    public EmailLog send(Long participantId, String email, String subject) {
        // Try to send email if mailSender is available; otherwise log to stdout
        if (mailSender != null) {
            try {
                SimpleMailMessage message = new SimpleMailMessage();
                message.setTo(email);
                message.setSubject(subject);
                message.setText("Hello,\n\n" + subject + "\n\nRegards,\nRecruitment Team");
                mailSender.send(message);
            } catch (Exception e) {
                System.err.println("Failed to send email to " + email + ": " + e.getMessage());
            }
        } else {
            System.out.println("[MockEmailService] Pretend-sending email to " + email + " subject='" + subject + "'");
        }

        EmailLog log = EmailLog.builder()
                .participantId(participantId)
                .email(email)
                .subject(subject)
                .sentTime(LocalDateTime.now())
                .build();
        return emailLogRepository.save(log);
    }
}
