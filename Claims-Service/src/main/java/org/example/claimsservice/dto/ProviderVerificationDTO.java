package org.example.claimsservice.dto;

import jakarta.validation.constraints.NotNull;
import org.example.claimsservice.model.enums.ProviderVerificationStatus;

public record ProviderVerificationDTO(
        @NotNull
        Long claimId,

        @NotNull
        Long providerId,

        ProviderVerificationStatus status
)

{}
