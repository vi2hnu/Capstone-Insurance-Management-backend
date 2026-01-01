package org.example.claimsservice.service.Implementation;

import java.util.List;

import feign.FeignException;
import lombok.extern.slf4j.Slf4j;
import org.example.claimsservice.dto.*;
import org.example.claimsservice.exception.*;
import org.example.claimsservice.feign.IdentityService;
import org.example.claimsservice.feign.PolicyService;
import org.example.claimsservice.feign.ProviderService;
import org.example.claimsservice.model.entity.Claim;
import org.example.claimsservice.model.entity.ClaimReview;
import org.example.claimsservice.model.enums.ClaimStage;
import org.example.claimsservice.model.enums.ClaimStatus;
import org.example.claimsservice.model.enums.ReviewStatus;
import org.example.claimsservice.model.enums.ReviewerRole;
import org.example.claimsservice.repository.ClaimRepository;
import org.example.claimsservice.repository.ClaimReviewRepository;
import org.example.claimsservice.service.ClaimService;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class ClaimServiceImpl implements ClaimService{

    private final ClaimRepository claimRepository;
    private final PolicyService policyService;
    private final ProviderService providerService;
    private final ClaimReviewRepository claimReviewRepository;
    private final IdentityService identityService;
    
    public ClaimServiceImpl(ClaimRepository claimRepository, PolicyService policyService,
                            ProviderService providerService, ClaimReviewRepository claimReviewRepository,
                            IdentityService identityService) {
        this.claimRepository = claimRepository;
        this.policyService = policyService;
        this.providerService = providerService;
        this.claimReviewRepository = claimReviewRepository;
        this.identityService = identityService;
    }
    
    @Override
    public Claim addClaim(AddClaimsDTO request) {
        UserDTO user = identityService.getUser(request.userId());
        if(user.bankAccount()==null){
            throw new NoBankDetailsFoundException("No bank account found");
        }

        PolicyDTO policy;
        try {
            policy = policyService.getPolicyById(request.policyId());
        } catch (FeignException.NotFound ex) {
            throw new PolicyNotFoundException("Policy not found");
        }

        if(policy==null || !policy.status().equals("ACTIVE")) {
            throw new PolicyNotFoundException("Policy not found");
        }

        if((policy.agentId()!=null && !policy.agentId().equals(request.agentId())) ||
                (policy.agentId()==null && request.agentId()!=null)){
            throw new InvalidPolicyClaimException("Agent id mismatch");
        }

        if(!policy.userId().equals(request.userId())
                || policy.remainingCoverage()<request.requestedAmount() ||
                !providerService.checkHospitalPlan(policy.plan().id(), request.hospitalId()) ) {
            throw new InvalidPolicyClaimException("Invalid policy claim");
        }

        if(claimRepository.existsByUserIdAndPolicyId(request.userId(),request.policyId())){
            throw new ClaimAlreadySubmittedException("Claim already submitted");
        }

        Claim claim = new Claim(request.policyId(), request.userId(), 
        request.hospitalId(), request.requestedAmount(), request.supportingDocument());
        return claimRepository.save(claim);
    }

    @Override
    public List<Claim> getClaimsByUserId(String userId) {
        return claimRepository.findByUserId(userId);
    }

    @Override
    public Claim getClaimById(Long id) {
        return claimRepository.findById(id).orElse(null);
    }

    @Override
    public List<Claim> getClaimByProviderId(Long providerId) {
        return claimRepository.findByHospitalId(providerId).stream()
                .filter(c -> c.getStage() == ClaimStage.PROVIDER).toList();
    }

    @Override
    public Claim providerVerification(ProviderVerificationDTO request) {
        Claim claim = claimRepository.findById(request.claimId())
                .orElseThrow(()-> new ClaimNotFoundException("Claim does not exist"));
        try {
           Boolean associated = providerService.checkAssociation(request.providerId(), claim.getHospitalId());
        }
        catch (FeignException.BadRequest ex) {
            throw new UnauthorizedClaimReviewException("User not associated with this provider");
        }

        if(!claim.getStage().equals(ClaimStage.PROVIDER)) {
            throw new InvalidStageException("Claim stage is not PROVIDER");
        }

        ClaimReview claimReview = new ClaimReview(ReviewerRole.PROVIDER,request.providerId(),
                request.status(),request.comments());

        claimReviewRepository.save(claimReview);

        claim.setProviderReview(claimReview);
        claim.setStage(ClaimStage.CLAIMS_OFFICER);
        claim.setStatus(ClaimStatus.IN_REVIEW);
        return claimRepository.save(claim);
    }

    @Override
    public Claim claimsOfficerValidation(ClaimsOfficerValidationDTO request) {
        Claim claim = claimRepository.findById(request.claimsId())
                .orElseThrow(()-> new ClaimNotFoundException("Claim does not exist"));

        if(!claim.getStage().equals(ClaimStage.CLAIMS_OFFICER)) {
            throw new InvalidStageException("Claim stage is not CLAIMS_OFFICER");
        }
        ClaimReview claimReview = new ClaimReview(ReviewerRole.CLAIMS_OFFICER,request.claimsOfficerId(),
                request.status(),request.comments());

        claimReviewRepository.save(claimReview);


        claim.setClaimsOfficerReview(claimReview);
        claim.setStatus(ClaimStatus.APPROVED);
        if(!request.status().equals(ReviewStatus.APPROVED)) {
            claim.setStatus(ClaimStatus.REJECTED);
            claim.setStage(ClaimStage.COMPLETED);
            return claimRepository.save(claim);
        }
        claim.setStage(ClaimStage.PAYMENT);

        //using kafka call the payment service

        return claimRepository.save(claim);
    }

    @Override
    public Claim changeStatus(Long claimId) {
        Claim claim = claimRepository.findById(claimId)
                .orElseThrow(()-> new ClaimNotFoundException("Claim does not exist"));

        claim.setStage(ClaimStage.COMPLETED);
        claim.setStatus(ClaimStatus.PAID);
        return claimRepository.save(claim);
    }


}
