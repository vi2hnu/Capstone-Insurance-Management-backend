package org.example.billingservice.configuration;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.razorpay.RazorpayClient;
import com.razorpay.RazorpayException;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Configuration
public class RazorPayConfiguration {

    @Value("${razorpay.key.id}")
    private String keyId;

    @Value("${razorpay.key.secret}")
    private String keySecret;

    @Bean
    public RazorpayClient razorpayClient() throws RazorpayException {
        try {
            return new RazorpayClient(keyId, keySecret);
        }
        catch (RazorpayException e) {
            log.error("Failed to create Razorpay client", e);
            throw new RazorpayException("Failed to create Razorpay client");
        }
    }
}
