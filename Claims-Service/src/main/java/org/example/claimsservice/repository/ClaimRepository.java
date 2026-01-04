package org.example.claimsservice.repository;

import java.time.LocalDate;
import java.util.List;

import org.example.claimsservice.model.entity.Claim;
import org.example.claimsservice.model.enums.ClaimStage;
import org.example.claimsservice.model.enums.ClaimSubmissionEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface ClaimRepository extends JpaRepository<Claim, Long> {
    List<Claim> findByUserId(String userId);
    Boolean existsByUserIdAndPolicyId(String userId, Long policyId);
    List<Claim> findByHospitalId(Long hospitalId);
    List<Claim> findByStage(ClaimStage stage);
    List<Claim> findByHospitalIdAndSubmittedBy(Long hospitalId,ClaimSubmissionEntity submittedby);

    @Query("SELECT c.status, COUNT(c.id) " +
           "FROM Claim c " +
           "GROUP BY c.status")
    List<Object[]> countClaimsByStatus();
    
    Page<Claim> findByHospitalIdOrderByClaimRequestDateAsc(Long hospitalId, Pageable pageable);
    Page<Claim> findByClaimRequestDateBetweenOrderByRequestedAmountDesc(LocalDate startDate,LocalDate endDate,
        Pageable pageable);
}
