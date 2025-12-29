package org.example.policyservice.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import org.example.policyservice.model.enums.Gender;

public record GetUserIdDTO(
        @NotBlank
        String name,

        @NotBlank
        @Email
        String email,

        Gender gender
) {
}
