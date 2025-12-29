package org.example.identityservice.dto;

import org.example.identityservice.model.enums.Gender;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record CheckUserDTO(
    @NotBlank
    String name, 
    
    @NotBlank
    @Email
    String email, 

    Gender gender) 
{}
