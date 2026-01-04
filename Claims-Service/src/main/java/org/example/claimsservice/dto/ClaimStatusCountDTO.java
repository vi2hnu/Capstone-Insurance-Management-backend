package org.example.claimsservice.dto;

import org.example.claimsservice.model.enums.ClaimStatus;

public record ClaimStatusCountDTO(
    ClaimStatus status,
    long count
) {
    
}
