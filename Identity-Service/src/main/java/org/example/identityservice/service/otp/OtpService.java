package org.example.identityservice.service.otp;

import org.example.identityservice.dto.ValidateOtpDTO;

public interface OtpService {
    String generateOtp(String email);
    boolean verifyOtp(ValidateOtpDTO dto);
}
