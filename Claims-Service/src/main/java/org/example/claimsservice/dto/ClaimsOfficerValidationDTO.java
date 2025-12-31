package org.example.claimsservice.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.example.claimsservice.model.enums.ReviewStatus;

public record ClaimsOfficerValidationDTO(
        @NotNull
        Long claimsId,

        @NotBlank
        String claimsOfficerId,

        ReviewStatus status,

        String comments
)
{}
