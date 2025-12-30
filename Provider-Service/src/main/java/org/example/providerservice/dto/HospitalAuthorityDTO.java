package org.example.providerservice.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record HospitalAuthorityDTO(
        
        @NotNull
        Long hospitalId,

        @NotBlank
        String userId
) {}
