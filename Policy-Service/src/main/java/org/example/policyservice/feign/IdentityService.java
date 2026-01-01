package org.example.policyservice.feign;

import org.example.policyservice.dto.GetUserIdDTO;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.PostMapping;

@Component
@org.springframework.cloud.openfeign.FeignClient(name = "Identity-Service")
public interface IdentityService {
    @PostMapping("/api/auth/check/user") //this api either creates user or check existing user and returns id
    String getUserId(GetUserIdDTO user);
}
