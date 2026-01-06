package org.example.claimsservice.service.implementation;

import java.time.LocalDateTime;
import java.util.List;

import org.example.claimsservice.dto.ClaimDTO;
import org.example.claimsservice.dto.ClaimStatusCountDTO;
import org.example.claimsservice.dto.UserDTO;
import org.example.claimsservice.feign.IdentityService;
import org.example.claimsservice.model.enums.ClaimStatus;
import org.example.claimsservice.repository.ClaimRepository;
import org.example.claimsservice.service.AnalyticService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

@Service
public class AnalyticServiceImpl implements AnalyticService {

    private final ClaimRepository claimRepository;
    private final IdentityService identityService;

    public AnalyticServiceImpl(ClaimRepository claimRepository, IdentityService identityService) {
        this.claimRepository = claimRepository;
        this.identityService = identityService;
    }

    @Override
    public List<ClaimStatusCountDTO> getClaimCountByStatus() {
        List<Object[]> results = claimRepository.countClaimsByStatus();

        return results.stream()
                .map(row -> new ClaimStatusCountDTO((ClaimStatus) row[0], (Long) row[1])).toList();
    }

    @Override
    public Page<ClaimDTO> getClaimsByHospital(Long hospitalId, int page, int size) {
        return claimRepository
                .findByHospitalIdOrderByClaimRequestDateAsc(
                        hospitalId,
                        PageRequest.of(page, size)
                )
                .map(claim -> {
                    UserDTO user = identityService.getUser(claim.getUserId());
                    return ClaimDTO.fromEntity(
                            claim,
                            user != null ? user.username() : null
                    );
                });
    }

    @Override
    public List<ClaimDTO> getTopHighValueClaimsLastMonth() {
        LocalDateTime fromDate = LocalDateTime.now().minusMonths(1);
        PageRequest top10 = PageRequest.of(0, 10);
        return claimRepository.findTopHighValueClaimsLastMonth(fromDate, top10).stream()
                .map(claim -> {
                    UserDTO user = identityService.getUser(claim.getUserId());
                    return ClaimDTO.fromEntity(
                            claim,
                            user != null ? user.username() : null
                    );
                })
                .toList();
    }
}
