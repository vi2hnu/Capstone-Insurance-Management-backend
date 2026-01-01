package org.example.emailservice.dto;

import java.time.LocalDate;

public record PolicyDTO(
    PlanDTO plan,
    String userId,
    LocalDate startDate,
    LocalDate endDate,
    int renewalCount
) {
}
