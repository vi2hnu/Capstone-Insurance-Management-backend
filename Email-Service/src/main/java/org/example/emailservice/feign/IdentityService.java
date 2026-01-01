package org.example.emailservice.feign;

import org.example.emailservice.dto.UserDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "identity-service")
public interface IdentityService {

    @GetMapping("/api/user/get/user/{userId}")
    UserDTO getUserById(@PathVariable String userId);
}
