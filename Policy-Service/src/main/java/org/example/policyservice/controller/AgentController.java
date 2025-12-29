package org.example.policyservice.controller;

import jakarta.validation.Valid;
import org.example.policyservice.dto.GetUserIdDTO;
import org.example.policyservice.feign.FeignClient;
import org.example.policyservice.service.PolicyService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/agent")
public class AgentController {

    private final FeignClient feignClient;

    public AgentController(FeignClient feignClient, PolicyService policyService) {
        this.feignClient = feignClient;
    }

    @PostMapping("/get/user")
    public ResponseEntity<String> enrollUser(@RequestBody  @Valid GetUserIdDTO request){
        return ResponseEntity.status(HttpStatus.OK).body(feignClient.getUserId(request));
    }
}
