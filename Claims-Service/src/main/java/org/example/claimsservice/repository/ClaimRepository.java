package org.example.claimsservice.repository;

import java.util.List;

import org.example.claimsservice.model.entity.Claim;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ClaimRepository extends JpaRepository<Claim, Long> {
    List<Claim> findByUserId(String userId);
    Boolean existsByUserIdAndPolicyId(String userId, Long policyId);
    List<Claim> findByHospitalId(Long hospitalId);
}
