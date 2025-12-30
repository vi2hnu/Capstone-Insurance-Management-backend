package org.example.claimsservice.feign;

import org.example.claimsservice.dto.PolicyDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "Policy-Service")
public interface PolicyService {

    @GetMapping("/api/policy/get/{id}")
    PolicyDTO getPolicyById(@PathVariable Long id);
}
