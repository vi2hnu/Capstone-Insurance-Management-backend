package org.example.policyservice.controller;

import jakarta.validation.Valid;
import org.example.policyservice.dto.CoverageChangeDTO;
import org.example.policyservice.dto.PolicyUserDTO;
import org.example.policyservice.dto.PolicyEnrollDTO;
import org.example.policyservice.model.entity.Policy;
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
    public ResponseEntity<Policy> enrollUser(@RequestBody @Valid PolicyEnrollDTO request){
        return ResponseEntity.status(HttpStatus.CREATED).body(policyService.enrollUser(request));
    }

    @DeleteMapping("/cancel")
    public ResponseEntity<Void> cancelPolicy(@RequestBody @Valid PolicyUserDTO requesst){
        policyService.cancelPolicy(requesst);
        return new ResponseEntity<Void>(HttpStatus.NO_CONTENT);
    }

    @PostMapping("/renew")
    public ResponseEntity<Policy> renewPolicy(@RequestBody @Valid PolicyUserDTO request){
        return ResponseEntity.status(HttpStatus.CREATED).body(policyService.renewPolicy(request));
    }

    @GetMapping("/get/all/{userId}")
    public ResponseEntity<List<Policy>> getAllEnrolledPolicy(@PathVariable String userId){
        return ResponseEntity.status(HttpStatus.OK).body(policyService.viewAllRegisteredPolicies(userId));
    }

    @PutMapping("/change/claimed-amount")
    public ResponseEntity<Policy> changeClaimedAmount(@RequestBody @Valid CoverageChangeDTO request){
        return ResponseEntity.status(HttpStatus.OK).body(policyService.changeCoverage(request));
    }

    @GetMapping("/get/{policyId}")
    public ResponseEntity<Policy> getPolicy(@PathVariable Long policyId){
        return ResponseEntity.status(HttpStatus.OK).body(policyService.getPolicyById(policyId));
    }

    @GetMapping("/check/enrollment/{userId}/{planId}")
    public ResponseEntity<Policy> getPolicy(@PathVariable String userId, @PathVariable Long planId){
        return ResponseEntity.status(HttpStatus.OK).body(policyService.getEnrollment(userId, planId));
    } 
}
