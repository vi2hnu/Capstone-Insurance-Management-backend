package org.example.identityservice.dto;

public record LoginRessultDTO(
        String token,
        Boolean changePassword,
        UserDTO user
) {}
