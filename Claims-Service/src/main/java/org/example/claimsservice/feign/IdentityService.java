package org.example.claimsservice.feign;

import org.example.claimsservice.dto.UserDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "Identity-Service")
public interface IdentityService {

    @GetMapping("/api/auth/get/user/{id}")
    public UserDTO getUser(@PathVariable String id);
}
