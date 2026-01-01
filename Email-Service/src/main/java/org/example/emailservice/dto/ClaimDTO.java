package org.example.emailservice.dto;

public record ClaimDTO(
    Long policyId,
    String userId,
    Double requestedAmount,
    String claimRequestDate,
    Long id,
    ClaimStatus status
){}
