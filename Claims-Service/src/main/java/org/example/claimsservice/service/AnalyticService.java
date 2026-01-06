package org.example.claimsservice.service;

import java.util.List;

import org.example.claimsservice.dto.ClaimDTO;
import org.example.claimsservice.dto.ClaimStatusCountDTO;
import org.springframework.data.domain.Page;

public interface AnalyticService{
    List<ClaimStatusCountDTO> getClaimCountByStatus();
    Page<ClaimDTO> getClaimsByHospital(Long hospitalId, int page, int size);
    List<ClaimDTO> getTopHighValueClaimsLastMonth();
}