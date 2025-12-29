package org.example.identityservice.dto;

import org.example.identityservice.model.enums.Gender;
import org.example.identityservice.model.enums.Role;

public record UserDTO(String username,String name, String email, Gender gender, Role role) {
}
