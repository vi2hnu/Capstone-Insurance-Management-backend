package org.example.identityservice.dto;

public record OtpMailDTO(
    String email,
    String otp
) {
    
}
