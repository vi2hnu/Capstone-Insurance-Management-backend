package org.example.claimsservice.dto;

import org.example.claimsservice.model.entity.BankAccount;

public record UserDTO(
        String id,
        String username,
        String name,
        String email,
        BankAccount bankAccount) {
}