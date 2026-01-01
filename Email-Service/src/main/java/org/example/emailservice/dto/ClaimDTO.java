package org.example.emailservice.dto;

import java.time.LocalDateTime;

public record ClaimDTO(
    Long policyId,
    String userId,
    Double requestedAmount,
    LocalDateTime claimRequestDate,
    Long id,
    ClaimStatus status
){}
