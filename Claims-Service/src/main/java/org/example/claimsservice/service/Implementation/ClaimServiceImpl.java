package org.example.claimsservice.service.Implementation;

import java.util.List;

import org.example.claimsservice.dto.AddClaimsDTO;
import org.example.claimsservice.dto.PolicyDTO;
import org.example.claimsservice.exception.InvalidPolicyClaimException;
import org.example.claimsservice.exception.PolicyNotFoundException;
import org.example.claimsservice.feign.PolicyService;
import org.example.claimsservice.feign.ProviderService;
import org.example.claimsservice.model.entity.Claim;
import org.example.claimsservice.repository.ClaimRepository;
import org.example.claimsservice.service.ClaimService;
import org.springframework.stereotype.Service;

@Service
public class ClaimServiceImpl implements ClaimService{

    private final ClaimRepository claimRepository;
    private final PolicyService policyService;
    private final ProviderService providerService;
    
    public ClaimServiceImpl(ClaimRepository claimRepository, PolicyService policyService,
                            ProviderService providerService) {
        this.claimRepository = claimRepository;
        this.policyService = policyService;
        this.providerService = providerService;
    }
    
    @Override
    public Claim addClaim(AddClaimsDTO request) {

        PolicyDTO policy = policyService.getPolicyById(request.policyId());
        if(policy == null || !policy.status().equals("ACTIVE")) {
            throw new PolicyNotFoundException("Policy not found");
        }

        if(policy.agentId()!=null && !policy.agentId().equals(request.agentId())){
            throw new InvalidPolicyClaimException("Agent id mismatch");
        }

        if(!policy.userId().equals(request.userId())
                || policy.remainingCoverage()<request.requestedAmount() ||
                !providerService.checkHospitalPlan(policy.plan().id(), request.hospitalId()) ) {
            throw new InvalidPolicyClaimException("Invalid policy claim");
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
    
}
