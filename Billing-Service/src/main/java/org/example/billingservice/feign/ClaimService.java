package org.example.billingservice.feign;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;

@FeignClient(name = "Claims-Service")
public interface ClaimService {

    @PutMapping("/api/claim/mark/paid/{claimId}")
    void markAsPaid(@PathVariable Long claimId);
}
