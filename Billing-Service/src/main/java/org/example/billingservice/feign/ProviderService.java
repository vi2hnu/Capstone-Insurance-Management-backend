package org.example.billingservice.feign;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "Provider-Service")
public interface ProviderService {

    @GetMapping("/api/provider/get/type/{planId}/{providerId}")
    String getProviderType(@PathVariable Long planId, @PathVariable Long providerId);
}
