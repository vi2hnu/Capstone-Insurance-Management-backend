package org.example.claimsservice.controller;

import jakarta.validation.Valid;
import org.example.claimsservice.dto.ClaimsOfficerValidationDTO;
import org.example.claimsservice.model.entity.Claim;
import org.example.claimsservice.service.ClaimService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/claims-officer")
public class ClaimsOfficerController {
    private final ClaimService claimService;
    public ClaimsOfficerController(ClaimService claimService) {
        this.claimService = claimService;
    }

    @PutMapping("/validate/claim")
    public ResponseEntity<Claim> validateClaim(@RequestBody @Valid ClaimsOfficerValidationDTO request) {
        return ResponseEntity.status(HttpStatus.OK).body(claimService.claimsOfficerValidation(request));
    }
}
