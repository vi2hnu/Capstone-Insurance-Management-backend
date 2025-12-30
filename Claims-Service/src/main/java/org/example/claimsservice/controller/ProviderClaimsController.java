package org.example.claimsservice.controller;

import jakarta.validation.Valid;
import org.example.claimsservice.dto.ProviderVerificationDTO;
import org.example.claimsservice.model.entity.Claim;
import org.example.claimsservice.service.ClaimService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

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
}
