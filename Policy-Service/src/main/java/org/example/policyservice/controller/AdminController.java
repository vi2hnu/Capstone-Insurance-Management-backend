package org.example.policyservice.controller;

import java.util.List;

import org.example.policyservice.dto.PlanCountDTO;
import org.example.policyservice.dto.PlanDTO;
import org.example.policyservice.dto.PolicyStatusCountDTO;
import org.example.policyservice.model.entity.Plan;
import org.example.policyservice.service.PlanService;
import org.example.policyservice.service.implementation.PolicyServiceImpl;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/admin")
public class AdminController {

    private final PlanService planService;
    private final PolicyServiceImpl polyicService;

    public AdminController(PlanService planService, PolicyServiceImpl polyicService) {
        this.planService = planService;
        this.polyicService = polyicService;
    }

    @PostMapping("/plan/add")
    public ResponseEntity<Plan> addPlan(@RequestBody @Valid PlanDTO request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(planService.addPlan(request));
    }

    @GetMapping("/plan/analytics")
    public ResponseEntity<List<PlanCountDTO>> getAnalytics() {
        return ResponseEntity.status(HttpStatus.OK).body(polyicService.getMostPurchasedPlansLastMonth());
    }

    @GetMapping("/get/policies/by-status")
    public ResponseEntity<List<PolicyStatusCountDTO>> getPoliciesByStatus() {
        return ResponseEntity.status(HttpStatus.OK).body(polyicService.getPolicyCountByStatus());
    }
}
