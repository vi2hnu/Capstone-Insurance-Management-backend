package org.example.policyservice.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record PolicyEnrollDTO(

        @NotBlank
        String userId,

        @NotNull
        Long planId
)
{}
