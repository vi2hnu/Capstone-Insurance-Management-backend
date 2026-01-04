package org.example.claimsservice.controller;

import java.util.List;

import org.example.claimsservice.dto.AddClaimsDTO;
import org.example.claimsservice.dto.ProviderVerificationDTO;
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
@RequestMapping("/api/provider")
public class ProviderClaimsController {

    private final ClaimService claimService;
    public ProviderClaimsController(ClaimService claimService) {
        this.claimService = claimService;
    }

    @GetMapping("/get/all/claims/{providerId}")
    public ResponseEntity<List<Claim>> getAllClaims(@PathVariable Long providerId) {
        return ResponseEntity.status(HttpStatus.OK).body(claimService.getClaimByProviderId(providerId));
    }

    @PutMapping("/claim/verify")
    public ResponseEntity<Claim> verifyClaim(@RequestBody @Valid ProviderVerificationDTO request) {
        return ResponseEntity.status(HttpStatus.OK).body(claimService.providerVerification(request));
    }

    @PostMapping("/add/claim")
    public ResponseEntity<Claim> addClaim(@RequestBody @Valid AddClaimsDTO request) {
        return ResponseEntity.status(HttpStatus.OK).body(claimService.providerAddClaim(request));
    }

    @GetMapping("/get/submitted/claims/{providerId}")
    public ResponseEntity<List<Claim>> getSubmittedClaim(@PathVariable Long providerId) {
        return ResponseEntity.status(HttpStatus.OK).body(claimService.getSubmittedClaimsOfProvider(providerId));
    }
}
