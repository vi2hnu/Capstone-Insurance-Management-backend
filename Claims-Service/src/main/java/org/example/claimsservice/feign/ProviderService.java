package org.example.claimsservice.feign;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "Provider-Service")
public interface ProviderService {

    @GetMapping("/api/provider/check/{planId}/{hospitalId}")
    Boolean checkHospitalPlan(@PathVariable Long planId, @PathVariable Long hospitalId);
}
