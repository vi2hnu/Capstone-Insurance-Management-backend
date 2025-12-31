package org.example.identityservice.dto;

import org.example.identityservice.model.entity.BankAccount;
import org.example.identityservice.model.enums.Gender;
import org.example.identityservice.model.enums.Role;

public record UserDTO(
    String id, 
    String username, 
    String name, 
    String email, 
    Gender gender, 
    Role role,
    BankAccount bankAccount) {
}