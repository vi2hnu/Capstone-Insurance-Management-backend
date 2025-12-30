package org.example.claimsservice.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record AddClaimsDTO(

    @NotNull
    Long policyId,

    @NotBlank
    String userId,

    @NotNull
    Long hospitalId,

    @NotNull
    Double requestedAmount,
    
    String supportingDocument,

    String agentId

) {}
