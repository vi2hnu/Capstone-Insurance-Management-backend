package org.example.claimsservice.dto;

import jakarta.validation.constraints.NotNull;
import org.example.claimsservice.model.enums.ReviewStatus;

public record ProviderVerificationDTO(
        @NotNull
        Long claimId,

        @NotNull
        String providerId,

        ReviewStatus status,

        String comments


)

{}
