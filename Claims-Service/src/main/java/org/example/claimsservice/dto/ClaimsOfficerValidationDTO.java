package org.example.claimsservice.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.example.claimsservice.model.enums.ClaimStatus;

public record ClaimsOfficerValidationDTO(
        @NotNull
        Long claimsId,

        @NotBlank
        String claimsOfficerId,

        ClaimStatus claimStatus
)
{}
