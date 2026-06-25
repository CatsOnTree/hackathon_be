package com.hackathon.controller;

import com.hackathon.dto.PanelistRequest;
import com.hackathon.entity.Panelist;
import com.hackathon.service.PanelistService;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/panelists")
public class PanelistController {

    private final PanelistService panelistService;

    public PanelistController(PanelistService panelistService) {
        this.panelistService = panelistService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("hasRole('ADMIN')")
    public Panelist create(@Valid @RequestBody PanelistRequest request) {
        return panelistService.create(request);
    }

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public List<Panelist> findAll() {
        return panelistService.findAll();
    }
}
