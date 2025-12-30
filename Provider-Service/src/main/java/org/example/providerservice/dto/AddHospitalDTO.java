package org.example.providerservice.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record AddHospitalDTO(
        @NotBlank
        String hospitalName,

        @NotBlank
        String cityName,

        @NotBlank
        String phoneNumber,

        @NotBlank
        @Email
        String email
)
{}
