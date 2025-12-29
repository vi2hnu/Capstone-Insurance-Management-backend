package org.example.identityservice.dto;

public record ChangePasswordDTO (
        String username,
        String oldPassword,
        String newPassword
){}
