package org.example.identityservice.dto;

import jakarta.validation.constraints.NotBlank;

public record AddBankDTO(
    @NotBlank
    String userId,

    @NotBlank
    String bankName, 

    @NotBlank
    String accountNumber, 

    @NotBlank
    String ifscCode
) {}
