package org.example.policyservice.controller;

import jakarta.validation.Valid;
import org.example.policyservice.dto.PolicyUserDTO;
import org.example.policyservice.dto.PolicyEnrollDTO;
import org.example.policyservice.model.entity.PolicyUser;
import org.example.policyservice.service.PolicyService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/policy")
public class PolicyController {

    private final PolicyService policyService;

    public PolicyController(PolicyService policyService){
        this.policyService = policyService;
    }

    @PostMapping("/enroll")
    public ResponseEntity<PolicyUser> enrollUser(@RequestBody @Valid PolicyEnrollDTO request){
        return ResponseEntity.status(HttpStatus.OK).body(policyService.enrollUser(request));
    }

    @PostMapping("/cancel")
    public ResponseEntity<Void> cancelPolicy(@RequestBody @Valid PolicyUserDTO requesst){
        policyService.cancelPolicy(requesst);
        return new ResponseEntity<Void>(HttpStatus.NO_CONTENT);
    }

    @PostMapping("/renew")
    public ResponseEntity<PolicyUser> renewPolicy(@RequestBody @Valid PolicyUserDTO request){
        return ResponseEntity.status(HttpStatus.OK).body(policyService.renewPolicy(request));
    }

    @GetMapping("/get/{userId}")
    public ResponseEntity<List<PolicyUser>> getAllEnrolledPolicy(@PathVariable String userId){
        return ResponseEntity.status(HttpStatus.OK).body(policyService.viewAllRegisteredPolicies(userId));
    }
}
