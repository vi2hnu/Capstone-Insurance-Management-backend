package org.example.policyservice.feign;

import org.example.policyservice.dto.GetUserIdDTO;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.PostMapping;

@Component
@org.springframework.cloud.openfeign.FeignClient(name = "Identity-Service")
public interface FeignClient {
    @PostMapping("/api/auth/check/user")
    String getUserId(GetUserIdDTO user);
}
