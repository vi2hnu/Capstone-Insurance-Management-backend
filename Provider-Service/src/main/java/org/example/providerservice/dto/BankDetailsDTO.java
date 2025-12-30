package org.example.providerservice.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record BankDetailsDTO(

    @NotBlank
    String userId,

    @NotNull
    Long hospitalId,

    @NotBlank
    String bankName,

    @NotBlank
    String accountNumber,

    @NotBlank
    String ifsc
) {}
