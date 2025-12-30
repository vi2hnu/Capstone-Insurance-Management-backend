package org.example.claimsservice.service.Implementation;

import java.util.List;

import org.example.claimsservice.dto.AddClaimsDTO;
import org.example.claimsservice.model.entity.Claim;
import org.example.claimsservice.repository.ClaimRepository;
import org.example.claimsservice.service.ClaimService;
import org.springframework.stereotype.Service;

@Service
public class ClaimServiceImpl implements ClaimService{

    private final ClaimRepository claimRepository;
    
    public ClaimServiceImpl(ClaimRepository claimRepository) {
        this.claimRepository = claimRepository;
    }
    
    @Override
    public Claim addClaim(AddClaimsDTO request) {
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
