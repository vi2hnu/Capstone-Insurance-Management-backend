package org.example.billingservice.dto;

public record PolicyPlanDTO(
        Plan plan
) {
    public record Plan(Long id) {}
}
