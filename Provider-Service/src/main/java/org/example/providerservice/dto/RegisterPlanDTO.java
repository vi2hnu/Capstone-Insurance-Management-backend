package org.example.providerservice.dto;

import org.example.providerservice.model.enums.NetworkType;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record RegisterPlanDTO(
    
    @NotBlank
    String userId,

    @NotNull
    Long hospitalId,

    @NotNull
    Long planId,

    NetworkType networkType
) {}
