package org.example.billingservice.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.example.billingservice.model.enums.Purpose;

public record CreateOrderDTO(
        @NotBlank
        String userId,

        @NotNull
        Double amount,

        Purpose purpose
)
{}
