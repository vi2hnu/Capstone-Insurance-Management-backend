package org.example.emailservice.dto;

public record PayoutDTO(
    String userId,
    Long claimId,
    Double amount
) {}
