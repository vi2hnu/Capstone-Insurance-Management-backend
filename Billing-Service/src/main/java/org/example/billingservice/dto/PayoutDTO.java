package org.example.billingservice.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record PayoutDTO(

        @NotNull
        Long claimId,

        @NotBlank
        String userId,

        @NotNull
        Long providerId,

        @NotNull
        Double amount,

        @NotNull
        Long policyId
)
{}
