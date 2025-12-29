package org.example.identityservice.service.otp;

import org.example.identityservice.dto.GenerateOtpDTO;
import org.example.identityservice.dto.ValidateOtpDTO;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;
import java.time.Duration;
import java.util.Objects;

@Service
public class OtpService {

    private final StringRedisTemplate redisTemplate;
    private static final SecureRandom random = new SecureRandom();

    public OtpService(StringRedisTemplate redisTemplate){
        this.redisTemplate  =redisTemplate;
    }

    public int randomNumbers() {
        return 100000 + random.nextInt(900000);
    }

    public String generateOtp(GenerateOtpDTO dto){
        String otp = String.valueOf(randomNumbers());
        redisTemplate.opsForValue().set(dto.email(),otp, Duration.ofMinutes(15));

        //kafka will later be implemented here to send the opt as email to user

        //we won't be sending opt to user in final version
        return otp;

    }

    public boolean verifyOtp(ValidateOtpDTO dto){
        String savedOtp = redisTemplate.opsForValue().get(dto.email());
        return Objects.equals(dto.otp(), savedOtp);
    }

}
