package org.example.billingservice.feign;

import org.example.billingservice.model.enums.ProviderType;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "Provider-Service")
public interface ProviderService {

    @GetMapping("/api/provider/get/type/{planId}/{providerId}")
    ProviderType getProviderType(@PathVariable Long planId, @PathVariable Long providerId);
}
