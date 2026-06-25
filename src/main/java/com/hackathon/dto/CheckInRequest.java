package com.hackathon.dto;

import jakarta.validation.constraints.NotBlank;

public record CheckInRequest(@NotBlank String participantCode) {
}
