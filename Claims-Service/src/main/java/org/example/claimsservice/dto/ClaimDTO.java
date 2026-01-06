package org.example.claimsservice.dto;

import java.time.LocalDateTime;

import org.example.claimsservice.model.entity.Claim;
import org.example.claimsservice.model.entity.ClaimReview;
import org.example.claimsservice.model.enums.ClaimStage;
import org.example.claimsservice.model.enums.ClaimStatus;
import org.example.claimsservice.model.enums.ClaimSubmissionEntity;

public record ClaimDTO(
        Long id,
        Long policyId,
        String userId,
        String username,
        Long hospitalId,
        ClaimStatus status,
        ClaimStage stage,
        Double requestedAmount,
        ClaimReview providerReview,
        ClaimReview claimsOfficerReview,
        ClaimSubmissionEntity submittedBy,
        String supportingDocument,
        LocalDateTime claimRequestDate
        ) {

    public static ClaimDTO fromEntity(Claim claim, String username) {
        if (claim == null) {
            return null;
        }

        return new ClaimDTO(
                claim.getId(),
                claim.getPolicyId(),
                claim.getUserId(),
                username,
                claim.getHospitalId(),
                claim.getStatus(),
                claim.getStage(),
                claim.getRequestedAmount(),
                claim.getProviderReview(),
                claim.getClaimsOfficerReview(),
                claim.getSubmittedBy(),
                claim.getSupportingDocument(),
                claim.getClaimRequestDate()
        );

    }
}
