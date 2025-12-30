package org.example.policyservice.dto;


import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record PolicyUserDTO(

        @NotBlank
        String userId,

        @NotNull
        Long policyId,

        String agentId
)
{}
