package org.example.policyservice.controller;

import jakarta.validation.Valid;
import org.example.policyservice.dto.GetUserIdDTO;
import org.example.policyservice.feign.IdentityService;
import org.example.policyservice.model.entity.Policy;
import org.example.policyservice.service.PolicyService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/agent")
public class AgentController {

    private final IdentityService identityService;
    private final PolicyService policyService;

    public AgentController(IdentityService identityService, PolicyService policyService) {
        this.identityService = identityService;
        this.policyService = policyService;
    }

    //agent first enters details of user to either create or get existing account
    @PostMapping("/get/user")
    public ResponseEntity<String> enrollUser(@RequestBody  @Valid GetUserIdDTO request){
        return ResponseEntity.status(HttpStatus.CREATED).body(identityService.getUserId(request));
    }

    @GetMapping("/get/all/enrollment/{agentId}")
    public ResponseEntity<List<Policy>> getAllEnrollment(@PathVariable String agentId){
        return ResponseEntity.status(HttpStatus.OK).body(policyService.getAllAgentEnrolledPolicies(agentId));
    }
}
