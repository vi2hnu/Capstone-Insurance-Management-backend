package org.example.claimsservice.controller;

import java.util.List;

import org.example.claimsservice.dto.AddClaimsDTO;
import org.example.claimsservice.model.entity.Claim;
import org.example.claimsservice.service.ClaimService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;




@RestController
@RequestMapping("/api/claim")
public class ClaimsController {

    private final ClaimService claimService;
    public ClaimsController(ClaimService claimService) {
        this.claimService = claimService;
    }

    @PostMapping("/add")
    public ResponseEntity<Claim> addClaim(@RequestBody @Valid AddClaimsDTO request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(claimService.addClaim(request));
    }
    
    @GetMapping("/user/{userId}")
    public ResponseEntity<List<Claim>> getClaimsOfUser(@PathVariable String userId) {
        return ResponseEntity.status(HttpStatus.OK).body(claimService.getClaimsByUserId(userId));
    }
    
    @GetMapping("/get/{id}")
    public ResponseEntity<Claim> getClaimById(@PathVariable Long id) {
        return ResponseEntity.status(HttpStatus.OK).body(claimService.getClaimById(id));
    }

    @PutMapping("/mark/paid/{claimId}")
    public ResponseEntity<Claim> markAsPaid(@PathVariable Long claimId) {
        return ResponseEntity.status(HttpStatus.OK).body(claimService.changeStatus(claimId));
    }
    
}
