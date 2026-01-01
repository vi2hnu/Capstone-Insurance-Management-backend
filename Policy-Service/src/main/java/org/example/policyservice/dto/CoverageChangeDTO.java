package org.example.policyservice.dto;

import jakarta.validation.constraints.NotNull;

public record CoverageChangeDTO(
        @NotNull
        Long policyId,

        @NotNull
        Double claimedAmount
)
{}
