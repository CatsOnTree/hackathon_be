package com.hackathon.dto;

import jakarta.validation.constraints.NotNull;

public record AssignmentRequest(
        @NotNull Long participantId,
        @NotNull Long panelistId
) {
}
