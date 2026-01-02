package org.example.billingservice.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record PayoutDTO(

        @NotNull
        Long id,

        @NotBlank
        String userId,

        @NotNull
        Long hospitalId,

        @NotNull
        Double requestedAmount,

        @NotNull
        Long policyId
)
{}
