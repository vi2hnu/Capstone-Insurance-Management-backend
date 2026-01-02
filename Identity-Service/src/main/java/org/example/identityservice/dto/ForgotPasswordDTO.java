package org.example.identityservice.dto;

import jakarta.validation.constraints.NotBlank;

public record ForgotPasswordDTO(
    @NotBlank
    String email,

    @NotBlank
    String password
) {
    
}
