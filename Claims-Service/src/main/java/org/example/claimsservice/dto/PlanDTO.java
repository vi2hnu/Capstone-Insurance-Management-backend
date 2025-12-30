package org.example.claimsservice.dto;

public record PlanDTO(
    String name,
    String description,
    Double premiumAmount,
    Double coverageAmount,
    Integer duration,
    String status,
    Long id
) {   
}
