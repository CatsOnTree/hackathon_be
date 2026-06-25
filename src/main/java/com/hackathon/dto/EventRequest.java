package com.hackathon.dto;

import com.hackathon.entity.EventStatus;
import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;

public record EventRequest(
        @NotBlank String name,
        String description,
        @NotNull @FutureOrPresent LocalDate eventDate,
        EventStatus status
) {
}
