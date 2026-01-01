package org.example.billingservice.dto;

import jakarta.validation.constraints.NotNull;

public record CoverageChangeDTO(
        @NotNull
        Long policyId,

        @NotNull
        Double claimedAmount
)
{}
