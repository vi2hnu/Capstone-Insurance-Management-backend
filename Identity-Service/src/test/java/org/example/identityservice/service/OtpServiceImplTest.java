package org.example.identityservice.service;


import java.time.Duration;

import org.example.identityservice.dto.OtpMailDTO;
import org.example.identityservice.dto.ValidateOtpDTO;
import org.example.identityservice.service.otp.OtpServiceImpl;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.kafka.core.KafkaTemplate;

@ExtendWith(MockitoExtension.class)
class OtpServiceImplTest {

    @Mock
    private StringRedisTemplate redisTemplate;

    @Mock
    private KafkaTemplate<String, OtpMailDTO> kafkaTemplate;

    @Mock
    private ValueOperations<String, String> valueOperations;

    @InjectMocks
    private OtpServiceImpl otpService;

    @Test
    void generateOtp_shouldGenerateSaveAndSendOtp() {
        String email = "test@example.com";
        when(redisTemplate.opsForValue()).thenReturn(valueOperations);

        String result = otpService.generateOtp(email);

        assertNotNull(result);
        assertEquals(6, result.length());
        
        verify(valueOperations).set(eq(email), eq(result), eq(Duration.ofMinutes(15)));
        verify(kafkaTemplate).send(eq("otp-email"), any(OtpMailDTO.class));
    }

    @Test
    void verifyOtp_shouldReturnTrue_whenOtpMatches() {
        String email = "test@example.com";
        String otp = "123456";
        ValidateOtpDTO dto = new ValidateOtpDTO(email, otp);

        when(redisTemplate.opsForValue()).thenReturn(valueOperations);
        when(valueOperations.get(email)).thenReturn(otp);

        boolean result = otpService.verifyOtp(dto);

        assertTrue(result);
    }

    @Test
    void verifyOtp_shouldReturnFalse_whenOtpMismatch() {
        String email = "test@example.com";
        String correctOtp = "123456";
        String wrongOtp = "654321";
        ValidateOtpDTO dto = new ValidateOtpDTO(email, wrongOtp);

        when(redisTemplate.opsForValue()).thenReturn(valueOperations);
        when(valueOperations.get(email)).thenReturn(correctOtp);

        boolean result = otpService.verifyOtp(dto);

        assertFalse(result);
    }

    @Test
    void verifyOtp_shouldReturnFalse_whenOtpExpiredOrNull() {
        String email = "test@example.com";
        ValidateOtpDTO dto = new ValidateOtpDTO(email, "123456");

        when(redisTemplate.opsForValue()).thenReturn(valueOperations);
        when(valueOperations.get(email)).thenReturn(null);

        boolean result = otpService.verifyOtp(dto);

        assertFalse(result);
    }
}