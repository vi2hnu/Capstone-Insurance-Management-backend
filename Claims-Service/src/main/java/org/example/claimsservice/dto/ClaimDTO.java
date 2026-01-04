package org.example.claimsservice.dto;

import org.example.claimsservice.model.enums.ClaimStage;
import org.example.claimsservice.model.enums.ClaimStatus;

public record ClaimDTO(
    Long claimId,
    String userId,
    String username,
    String hospitalId,
    Long hospitalName,
    ClaimStatus status,
    ClaimStage stage,
    Double requestedAmount
) {
    
}
