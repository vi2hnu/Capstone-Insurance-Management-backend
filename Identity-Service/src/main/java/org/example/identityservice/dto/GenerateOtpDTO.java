package org.example.identityservice.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record GenerateOtpDTO(

        @NotBlank
        @Email
        String email
) {
}
