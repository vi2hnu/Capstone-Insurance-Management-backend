package org.example.identityservice.service.otp;

import java.security.SecureRandom;
import java.time.Duration;
import java.util.Objects;

import org.example.identityservice.dto.ValidateOtpDTO;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

@Service
public class OtpServiceImpl implements OtpService {

    private final StringRedisTemplate redisTemplate;
    private static final SecureRandom random = new SecureRandom();

    public OtpServiceImpl(StringRedisTemplate redisTemplate){
        this.redisTemplate  =redisTemplate;
    }

    public int randomNumbers() {
        return 100000 + random.nextInt(900000);
    }

    @Override
    public String generateOtp(String email){
        String otp = String.valueOf(randomNumbers());
        redisTemplate.opsForValue().set(email,otp, Duration.ofMinutes(15));

        //kafka will later be implemented here to send the opt as email to user

        //we won't be sending opt to user in final version
        return otp;

    }

    @Override
    public boolean verifyOtp(ValidateOtpDTO dto){
        String savedOtp = redisTemplate.opsForValue().get(dto.email());
        return Objects.equals(dto.otp(), savedOtp);
    }

}
