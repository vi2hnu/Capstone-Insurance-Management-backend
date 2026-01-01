package org.example.emailservice.dto;

public record UserDTO(
        String name,
        String username,
        String email,
        String password
) {
}