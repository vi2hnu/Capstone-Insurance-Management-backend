package org.example.identityservice.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import org.example.identityservice.model.enums.Gender;
import org.example.identityservice.model.enums.Role;


public record CreateUserDTO(
    @NotBlank
    @Size(min = 3, max = 20)
    String name,

    @NotBlank
    @Size(min = 3, max = 20)
    String username,

    @NotBlank
    @Size(max = 50)
    @Email
    String email,

    @NotBlank
    Role role,

    @NotBlank
    @Size(min = 6, max = 40)
    String password,

    Gender gender
)
{}
