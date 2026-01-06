package org.example.claimsservice.controller;

import java.util.List;

import org.example.claimsservice.dto.ClaimStatusCountDTO;
import org.example.claimsservice.model.entity.Claim;
import org.example.claimsservice.service.AnalyticService;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/admin")
public class AnalyticController {

    private final AnalyticService analyticService;

    public AnalyticController(AnalyticService analyticService) {
        this.analyticService = analyticService;
    }

    @GetMapping("/get/claims/by-status")
    public ResponseEntity<List<ClaimStatusCountDTO>> getClaimsByStatus() {
        return ResponseEntity.status(HttpStatus.OK).body(analyticService.getClaimCountByStatus());
    }

    @GetMapping("/claims/by-hospital/{hospitalId}")
    public Page<Claim> getClaimsByHospital(@PathVariable Long hospitalId, @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        return analyticService.getClaimsByHospital(hospitalId, page, size);
    }

    @GetMapping("/claims/high-value/last-month")
    public List<Claim> getHighValueClaimsLastMonth() {
        return analyticService.getTopHighValueClaimsLastMonth();
    }

}
