package org.example.policyservice.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record PlanDTO(
        @NotBlank
        String name,

        @NotBlank
        String description,

        @NotNull
        @Min(100)
        Double premiumAmount,

        @NotNull
        @Min(1000)
        Double coverageAmount,

        @Min(6)
        int duration //in months
)

{}
