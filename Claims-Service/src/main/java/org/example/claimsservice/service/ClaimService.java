package org.example.claimsservice.service;

import java.util.List;

import org.example.claimsservice.dto.AddClaimsDTO;
import org.example.claimsservice.dto.ClaimDTO;
import org.example.claimsservice.dto.ClaimsOfficerValidationDTO;
import org.example.claimsservice.dto.ProviderVerificationDTO;
import org.example.claimsservice.model.entity.Claim;

public interface ClaimService {
    Claim addClaim(AddClaimsDTO request);
    List<Claim> getClaimsByUserId(String userId);
    Claim getClaimById(Long id);
    List<ClaimDTO> getClaimByProviderId(Long providerId);
    Claim providerVerification(ProviderVerificationDTO request);
    Claim claimsOfficerValidation(ClaimsOfficerValidationDTO request);
    Claim changeStatus(Long claimId);
    Claim providerAddClaim(AddClaimsDTO request);
    List<ClaimDTO> getClaimsForOfficer();
    List<Claim> getSubmittedClaimsOfProvider(Long providerId);
}
