package org.example.emailservice.dto;

public record PlanDTO(
    String name,
    String description,
    Double premiumAmount,
    Double coverageAmount,
    int duration
) {
}
