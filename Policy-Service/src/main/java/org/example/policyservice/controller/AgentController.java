package org.example.policyservice.controller;

import jakarta.validation.Valid;
import org.example.policyservice.dto.GetUserIdDTO;
import org.example.policyservice.feign.FeignClient;
import org.example.policyservice.model.entity.Policy;
import org.example.policyservice.service.PolicyService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/agent")
public class AgentController {

    private final FeignClient feignClient;
    private final PolicyService policyService;

    public AgentController(FeignClient feignClient, PolicyService policyService) {
        this.feignClient = feignClient;
        this.policyService = policyService;
    }

    @PostMapping("/get/user")
    public ResponseEntity<String> enrollUser(@RequestBody  @Valid GetUserIdDTO request){
        return ResponseEntity.status(HttpStatus.OK).body(feignClient.getUserId(request));
    }

    @GetMapping("/get/all/enrollment/{agentId}")
    public ResponseEntity<List<Policy>> getAllEnrollment(@PathVariable String agentId){
        return ResponseEntity.status(HttpStatus.OK).body(policyService.getAllAgentEnrolledPolicies(agentId));
    }
}
