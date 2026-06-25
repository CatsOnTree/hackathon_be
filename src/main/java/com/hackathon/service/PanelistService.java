package com.hackathon.service;

import com.hackathon.dto.PanelistRequest;
import com.hackathon.entity.Panelist;
import com.hackathon.entity.Role;
import com.hackathon.entity.User;
import com.hackathon.exception.BadRequestException;
import com.hackathon.repository.PanelistRepository;
import com.hackathon.repository.UserRepository;
import java.util.List;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class PanelistService {

    private final PanelistRepository panelistRepository;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public PanelistService(PanelistRepository panelistRepository, UserRepository userRepository,
            PasswordEncoder passwordEncoder) {
        this.panelistRepository = panelistRepository;
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Transactional
    public Panelist create(PanelistRequest request) {
        // Check if email already exists in users table
        if (userRepository.existsByEmail(request.email())) {
            throw new BadRequestException("Email already registered: " + request.email());
        }

        // Create panelist record
        Panelist panelist = panelistRepository.save(Panelist.builder()
                .name(request.name())
                .email(request.email())
                .domain(request.domain())
                .build());

        // Create user record for panelist with ROLE_PANELIST
        User user = User.builder()
                .username(request.email())
                .email(request.email())
                .password(passwordEncoder.encode(request.password()))
                .role(Role.ROLE_PANELIST)
                .build();

        userRepository.save(user);

        return panelist;
    }

    public List<Panelist> findAll() {
        return panelistRepository.findAll();
    }
}
