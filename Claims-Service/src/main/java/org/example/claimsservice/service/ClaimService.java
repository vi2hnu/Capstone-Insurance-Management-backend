package org.example.claimsservice.service;

import java.util.List;

import org.example.claimsservice.dto.AddClaimsDTO;
import org.example.claimsservice.model.entity.Claim;

public interface ClaimService {
    Claim addClaim(AddClaimsDTO request);
    List<Claim> getClaimsByUserId(String userId);
    Claim getClaimById(Long id);
}
