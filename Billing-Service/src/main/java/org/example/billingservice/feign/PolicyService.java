package org.example.billingservice.feign;

import org.example.billingservice.dto.CoverageChangeDTO;
import org.example.billingservice.dto.PolicyPlanDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(name = "Policy-Service")
public interface PolicyService {

    @GetMapping("/api/policy/get/{policyId}")
    PolicyPlanDTO getPolicy(@PathVariable Long policyId);

    @PutMapping("/api/policy/change/claimed-amount")
    void changeClaimedAmount(@RequestBody CoverageChangeDTO request);
}
