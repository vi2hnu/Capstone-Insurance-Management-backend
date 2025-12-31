package org.example.billingservice.dto;

import jakarta.validation.constraints.NotBlank;

public record VerifyPaymentDTO(
        @NotBlank
        String razorpayOrderId,

        @NotBlank
        String razorpayPaymentId,

        @NotBlank
        String razorpaySignature
) {}
