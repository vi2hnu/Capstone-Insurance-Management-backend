package org.example.claimsservice.dto;

import java.time.LocalDate;

public record PolicyDTO(
    PlanDTO plan,
    String userId,  
    LocalDate startDate,
    LocalDate endDate,
    String status,
    Double remainingCoverage,
    Integer renewalCounter,
    String agentId, 
    Long id
) {
    
}
