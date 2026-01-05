package org.example.claimsservice.service;

import java.util.List;

import org.example.claimsservice.dto.ClaimStatusCountDTO;
import org.example.claimsservice.model.entity.Claim;
import org.springframework.data.domain.Page;

public interface AnalyticService{
    List<ClaimStatusCountDTO> getClaimCountByStatus();
    Page<Claim> getClaimsByHospital(Long hospitalId, int page, int size);
    List<Claim> getTopHighValueClaimsLastMonth();
}