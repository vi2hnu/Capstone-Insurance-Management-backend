package org.example.identityservice.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import org.example.identityservice.model.enums.Gender;

public record SignupDTO(
        @NotBlank
        String name,

        @NotBlank
        @Size(min = 3, max = 20)
        String username,

        @NotBlank
        @Size(max = 50)
        @Email
        String email,

        @NotBlank
        @Size(min = 6, max = 40)
        String password,

        Gender gender
) {}